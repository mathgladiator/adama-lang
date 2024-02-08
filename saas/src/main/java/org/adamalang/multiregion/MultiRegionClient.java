/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.multiregion;

import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.*;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.runtime.sys.AuthResponse;
import org.adamalang.runtime.sys.ConnectionMode;
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.util.TreeMap;
import java.util.function.Consumer;

/** isolates the client away from the API level to control routing */
public class MultiRegionClient {
  private static final Logger LOG = LoggerFactory.getLogger(MultiRegionClient.class);
  private final SimpleExecutor executor;
  private final LocalRegionClient local;
  private final String region;
  private final PrivateKey privateKey;
  private final int keyId;
  private final TreeMap<String, SelfClient> remoteRegions;

  public MultiRegionClient(LocalRegionClient local, String region, PrivateKey privateKey, int keyId, TreeMap<String, SelfClient> remoteRegions) {
    this.executor = SimpleExecutor.create("multi-region-client");
    this.local = local;
    this.region = region;
    this.privateKey = privateKey;
    this.keyId = keyId;
    this.remoteRegions = remoteRegions;
  }

  public void shutdown() {
    executor.shutdown();
    local.shutdown();
  }

  public void deployLocal(String space) {
    local.getDeploymentTargets(space, (target) -> {
      try {
        // notify the client of an update
        local.notifyDeployment(target, space);
      } catch (Exception ex) {
        LOG.error("failed-deployment-write", ex);
      }
    }, 3);
  }

  private <T> SelfClient remoteForRegion(String region, Callback<T> callback) {
    SelfClient client = remoteRegions.get(region);
    if (client == null) {
      callback.failure(new ErrorCodeException(-1));
    }
    return client;
  }

  private <T> SelfClient remoteForRegion(String region, SimpleEvents callback) {
    SelfClient client = remoteRegions.get(region);
    if (client == null) {
      callback.error(-1);
    }
    return client;
  }

  public void waitForCapacity(String space, int timeout, Consumer<Boolean> finished) {
    local.waitForCapacity(space, timeout, finished);
  }

  public void reflect(String space, String key, Callback<String> callback) {
    local.finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            local.reflect(value.machine, space, key, callback);
            return;
          } else {

          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void create(AuthenticatedUser user, String space, String key, String entropy, String arg, Callback<Void> callback) {
    local.finder.find(new Key(space, key), new Callback<>() {
          @Override
          public void success(DocumentLocation value) {
            if (value.location == LocationType.Machine) {
              if (region.equals(value.region)) {
                local.create(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, entropy, arg, callback);
                return;
              } else {

              }
            }
            callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
          }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private Callback<ClientSeqResponse> wrapSeq(Callback<Integer> callback) {
    return new Callback<>() {
      @Override
      public void success(ClientSeqResponse response) {
        callback.success(response.seq);
      }
      @Override
      public void failure(ErrorCodeException ex) {
        callback.equals(ex);
      }
    };
  }

  private Callback<ClientSimpleResponse> wrapVoid(Callback<Void> callback) {
    return new Callback<>() {
      @Override
      public void success(ClientSimpleResponse response) {
        callback.success(null);
      }
      @Override
      public void failure(ErrorCodeException ex) {
        callback.equals(ex);
      }
    };
  }

  public void directSend(AuthenticatedUser user, String space, String key, String marker, String channel, String message, Callback<Integer> callback) {
    local.finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            local.directSend(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, marker, channel, message, callback);
            return;
          } else {
            SelfClient remote = remoteForRegion(value.region, callback);
            String identity = user.asIdentity(keyId, privateKey);
            if (remote != null) {
              if (marker == null) {
                ClientMessageDirectSendRequest request = new ClientMessageDirectSendRequest();
                request.identity = identity;
                request.space = space;
                request.key = key;
                request.channel = channel;
                request.message = Json.parse(message);
                remote.messageDirectSend(request, wrapSeq(callback));
              } else {
                ClientMessageDirectSendOnceRequest request = new ClientMessageDirectSendOnceRequest();
                request.identity = identity;
                request.space = space;
                request.key = key;
                request.channel = channel;
                request.dedupe = marker;
                request.message = Json.parse(message);
                remote.messageDirectSendOnce(request, wrapSeq(callback));
              }
              return;
            }
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void delete(AuthenticatedUser user, String space, String key, Callback<Void> callback) {
    local.finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            local.delete(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, callback);
            return;
          } else {
            SelfClient remote = remoteForRegion(value.region, callback);
            if (remote != null) {
              String identity = user.asIdentity(keyId, privateKey);
              ClientDocumentDeleteRequest request = new ClientDocumentDeleteRequest();
              request.identity = identity;
              request.space = space;
              request.key = key;
              remote.documentDelete(request, wrapVoid(callback));
              return;
            }
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });

  }

  public AdamaStream connect(AuthenticatedUser user, String space, String key, String viewerState, SimpleEvents events) {
    DelayAdamaStream stream = new DelayAdamaStream(executor, local.metrics.multi_region_find);
    local.finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            stream.ready(local.connect(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, ConnectionMode.Full, events));
            return;
          } else {
            SelfClient remote = remoteForRegion(value.region, events);
            if (remote != null) {
              ClientConnectionCreateRequest request = new ClientConnectionCreateRequest();
              request.identity = user.asIdentity(keyId, privateKey);
              request.space = space;
              request.key = key;
              request.viewerState = Json.parseJsonObject(viewerState);
              remote.connectionCreate(request, new Callback<SelfClient.DocumentStreamHandler>() {
                    @Override
                    public void success(SelfClient.DocumentStreamHandler value) {
                      stream.ready(new AdamaStream() {
                        @Override
                        public void update(String newViewerState) {
                          ClientConnectionUpdateRequest update = new ClientConnectionUpdateRequest();
                          update.viewerState = Json.parseJsonObject(newViewerState);
                          value.update(update, wrapVoid(Callback.DONT_CARE_VOID));
                        }

                        @Override
                        public void send(String channel, String marker, String message, Callback<Integer> callback) {
                          // TODO
                        }

                        @Override
                        public void password(String password, Callback<Integer> callback) {

                        }

                        @Override
                        public void canAttach(Callback<Boolean> callback) {

                        }

                        @Override
                        public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {

                        }

                        @Override
                        public void close() {

                        }
                      });
                      events.connected();
                    }

                    @Override
                    public void failure(ErrorCodeException ex) {
                      events.error(ex.code);
                    }
                  }, new Stream<ClientDataResponse>() {
                    @Override
                    public void next(ClientDataResponse value) {
                      events.delta(value.delta.toString());
                      // TODO: discover if we need to unpack the delta from the response
                    }

                    @Override
                    public void complete() {

                    }

                    @Override
                    public void failure(ErrorCodeException ex) {

                      events.disconnected();
                    }
                  });
              // TODO: return once this is setting up a stream
            }
          }
        }
        events.error(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        events.error(ex.code);
      }
    });
    return stream;
  }

  public void authorize(String ip, String origin, String space, String key, String username, String password, String new_password, Callback<String> callback) {
    local.finder.find(new Key(space, key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.location == LocationType.Machine) {
          if (location.region.equals(region)) {
            local.authorize(location.machine, ip, origin, space, key, username, password, new_password, callback);
            return;
          } else {
            // TODO: remote
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }


  public void authorization(String ip, String origin, String space, String key, String payload, Callback<AuthResponse> callback) {
    local.finder.find(new Key(space, key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.location == LocationType.Machine) {
          if (location.region.equals(region)) {
            local.authorization(location.machine, ip, origin, space, key, payload, callback);
            return;
          } else {
            // TODO: remote
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void webGet(String space, String key, WebGet request, Callback<WebResponse> callback) {
    local.finder.find(new Key(space, key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.location == LocationType.Machine) {
          if (location.region.equals(region)) {
            local.webGet(location.machine, space, key, request, callback);
            return;
          } else {
            // TODO: remote
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void webOptions(String space, String key, WebGet request, Callback<WebResponse> callback) {
    local.finder.find(new Key(space, key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.location == LocationType.Machine) {
          if (location.region.equals(region)) {
            local.webOptions(location.machine, space, key, request, callback);
            return;
          } else {
            // TODO: remote
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void webDelete(String space, String key, WebDelete request, Callback<WebResponse> callback) {
    local.finder.find(new Key(space, key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.location == LocationType.Machine) {
          if (location.region.equals(region)) {
            local.webDelete(location.machine, space, key, request, callback);
            return;
          } else {
            // TODO: remote
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void webPut(String space, String key, WebPut request, Callback<WebResponse> callback) {
    local.finder.find(new Key(space, key), new Callback<DocumentLocation>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.location == LocationType.Machine) {
          if (location.region.equals(region)) {
            local.webPut(location.machine, space, key, request, callback);
            return;
          } else {
            // TODO: remote
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.MULTI_REGION_CLIENT_NO_ROUTE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }
}
