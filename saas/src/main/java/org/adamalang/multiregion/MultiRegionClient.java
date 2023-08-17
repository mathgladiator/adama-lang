/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.multiregion;

import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.*;
import org.adamalang.contracts.data.AuthenticatedUser;
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
  private final SimpleFinderService finder;
  private final TreeMap<String, SelfClient> remoteRegions;

  public MultiRegionClient(LocalRegionClient local, String region, PrivateKey privateKey, int keyId, SimpleFinderService finder, TreeMap<String, SelfClient> remoteRegions) {
    this.executor = SimpleExecutor.create("multi-region-client");
    this.local = local;
    this.region = region;
    this.privateKey = privateKey;
    this.keyId = keyId;
    this.finder = finder;
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
    });
  }

  public void deployCrossRegion(AuthenticatedUser who, String space) {
    // TODO: find remote instances, and do a cross-region deployment...
    // TODO: also have each Adama host periodically download all spaces, and check if a deployment is needed
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
    local.reflect(space, key, callback);
  }

  public void create(AuthenticatedUser user, String space, String key, String entropy, String arg, Callback<Void> callback) {
    local.create(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, entropy, arg, callback);
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


  // MR-DONE
  public void directSend(AuthenticatedUser user, String space, String key, String marker, String channel, String message, Callback<Integer> callback) {
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            local.directSend(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, marker, channel, message, callback);
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
            }
          }
        } else {
          // TODO: this really shouldn't happen once the finder does a findbind
          local.directSend(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, marker, channel, message, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void delete(AuthenticatedUser user, String space, String key, Callback<Void> callback) {
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            local.delete(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, callback);
          } else {
            SelfClient remote = remoteForRegion(value.region, callback);
            if (remote != null) {
              String identity = user.asIdentity(keyId, privateKey);
              ClientDocumentDeleteRequest request = new ClientDocumentDeleteRequest();
              request.identity = identity;
              request.space = space;
              request.key = key;
              remote.documentDelete(request, wrapVoid(callback));
            }
          }
        } else {
          // TODO: find for placement
          local.delete(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });

  }

  public AdamaStream connect(AuthenticatedUser user, String space, String key, String viewerState, SimpleEvents events) {
    DelayAdamaStream stream = new DelayAdamaStream(executor, local.metrics.multi_region_find);
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        if (value.location == LocationType.Machine) {
          if (region.equals(value.region)) {
            stream.ready(local.connect(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
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
            }
          }
        } else {
          // TODO: this finder should ensure it always returns an appropriate machine
          stream.ready(local.connect(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        stream.ready(local.connect(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
      }
    });
    return stream;
  }

  public void authorize(String ip, String origin, String space, String key, String username, String password, Callback<String> callback) {
    local.authorize(ip, origin, space, key, username, password, callback);
  }
}
