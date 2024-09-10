/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.frontend.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.common.*;
import org.adamalang.contracts.data.DefaultPolicyBehavior;
import org.adamalang.contracts.data.DomainWithPolicy;
import org.adamalang.frontend.Session;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.BackupService;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.AuthResponse;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.features.UrlSummaryGenerator;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class GlobalDataHandler implements RootRegionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalDataHandler.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
  private final GlobalExternNexus nexus;
  private final Random rng;

  public GlobalDataHandler(GlobalExternNexus nexus) throws Exception {
    this.nexus = nexus;
    this.rng = new Random();
  }

  private void commonAuthorization(Session session, Key key, JsonNode payload, InitiationResponder responder) {
    try {
      ObjectNode node = (ObjectNode) payload;
      final String password = node.remove("password").textValue();
      nexus.adama.authorization(session.authenticator.getTransportContext().remoteIp, session.authenticator.getTransportContext().origin, key.space, key.key, node.toString(), new Callback<>() {
        @Override
        public void success(AuthResponse response) {
          if (response == null) {
            responder.error(new ErrorCodeException(ErrorCodes.DOCUMENT_AUTHORIIZE_FAILURE));
            return;
          }
          nexus.crypto.execute(new NamedRunnable("crypto-check") {
            @Override
            public void execute() throws Exception {
              if (SCryptUtil.check(password, response.hash)) {
                if (response.channel != null && response.success != null) {
                  AuthenticatedUser createdUser = new AuthenticatedUser(0, new NtPrincipal(response.agent, "doc/" + key.space + "/" + key.key), session.authenticator.getTransportContext());
                  nexus.adama.directSend(createdUser, key.space, key.key, null, response.channel, response.success, new Callback<Integer>() {
                    @Override
                    public void success(Integer value) {
                      responder.complete(nexus.signingKey.signDocumentIdentity(response.agent, key.space, key.key, 0));
                    }

                    @Override
                    public void failure(ErrorCodeException ex) {
                      responder.error(ex);
                    }
                  });
                } else {
                  responder.complete(nexus.signingKey.signDocumentIdentity(response.agent, key.space, key.key, 0));
                }
              } else {
                responder.error(new ErrorCodeException(ErrorCodes.DOCUMENT_AUTHORIIZE_FAILURE));
              }
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } catch (Exception ex) {
      responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_FAILED_PARSING_PASSWORD));
    }
  }

  private void commonAuthorize(Session session, Key key, String username, String password, String new_password, InitiationResponder responder) {
    nexus.adama.authorize(session.authenticator.getTransportContext().remoteIp, session.authenticator.getTransportContext().origin, key.space, key.key, username, password, new_password, new Callback<String>() {
      @Override
      public void success(String agent) {
        responder.complete(nexus.signingKey.signDocumentIdentity(agent, key.space, key.key, 0));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  private Domain domainToUse(DomainWithPolicy domainWithPolicy, InitiationResponder responder) {
    Domain domain = domainWithPolicy.domain;
    if (domain != null) {
      if (domain.key != null) {
        return domain;
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_DOMAIN_AUTH_NO_KEY_MAPPED));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_DOMAIN_AUTH_DOMAIN_INVALID_MAPPED));
    }
    return null;
  }

  @Override
  public void handle(Session session, IdentityStashRequest request, SimpleResponder responder) {
    if (request.identity.startsWith("cookie:")) {
      responder.error(new ErrorCodeException(ErrorCodes.AUTH_COOKIE_CANT_STASH_COOKIE));
      return;
    }
    if (request.who.context.identities == null) {
      responder.error(new ErrorCodeException(ErrorCodes.AUTH_IDENTITIES_NOT_AVAILABLE));
      return;
    }
    request.who.context.identities.put(request.name, request.identity);
    responder.complete();
  }

  @Override
  public void handle(Session session, IdentityHashRequest request, IdentityHashResponder responder) {
    String stringToHash = request.who.who.agent + ":" + request.who.who.authority + "/" + request.who.who.agent;
    MessageDigest digest = Hashing.sha384();
    digest.update(stringToHash.getBytes(StandardCharsets.UTF_8));
    responder.complete(Hashing.finishAndEncode(digest));
  }

  @Override
  public void handle(Session session, StatsRequest request, StatsResponder responder) {
    responder.next("created", session.created + "", "timestamp");
    responder.next("cached-identities-count", session.identityCache.size() + "", "int");
    ConnectionContext context = session.authenticator.getTransportContext();
    responder.next("origin", context.origin, "string");
    responder.next("remote-ip", context.remoteIp, "string");
    responder.next("cookie-identities-count", context.identities.size() + "", "int");
    responder.finish();
  }

  @Override
  public void handle(Session session, DocumentAuthorizationDomainRequest request, InitiationResponder responder) {
    Domain domain = domainToUse(request.resolvedDomain, responder);
    if (domain != null) {
      commonAuthorization(session, new Key(domain.space, domain.key), request.message, responder);
    }
  }

  @Override
  public void handle(Session session, DocumentAuthorizationRequest request, InitiationResponder responder) {
    commonAuthorization(session, new Key(request.space, request.key), request.message, responder);
  }

  @Override
  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder) {
    commonAuthorize(session, new Key(request.space, request.key), request.username, request.password, null, responder);
  }

  @Override
  public void handle(Session session, DocumentAuthorizeDomainRequest request, InitiationResponder responder) {
    Domain domain = domainToUse(request.resolvedDomain, responder);
    if (domain != null) {
      commonAuthorize(session, new Key(domain.space, domain.key), request.username, request.password, null, responder);
    }
  }

  @Override
  public void handle(Session session, DocumentAuthorizeWithResetRequest request, InitiationResponder responder) {
    commonAuthorize(session, new Key(request.space, request.key), request.username, request.password, request.new_password, responder);
  }

  @Override
  public void handle(Session session, DocumentAuthorizeDomainWithResetRequest request, InitiationResponder responder) {
    Domain domain = domainToUse(request.resolvedDomain, responder);
    if (domain != null) {
      commonAuthorize(session, new Key(domain.space, domain.key), request.username, request.password, request.new_password, responder);
    }
  }

  @Override
  public void handle(Session session, DocumentsHashPasswordRequest request, HashedPasswordResponder responder) {
    responder.complete(SCryptUtil.scrypt(request.password, 16384, 8, 1));
  }

  @Override
  public void handle(Session session, DocumentsCreateDedupeRequest request, DedupeResponder responder) {
    responder.complete(ProtectedUUID.generate());
  }

  @Override
  public void handle(Session session, DocumentDeleteRequest request, SimpleResponder responder) {
    nexus.adama.delete(request.who, request.space, request.key, new Callback<>() {
      @Override
      public void success(Void value) {
        responder.complete();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  private String fixEntropy(String entropy) {
    if (entropy == null) {
      return null;
    }
    try {
      return "" + Long.parseLong(entropy);
    } catch (NumberFormatException nfe) {
      return "" + entropy.hashCode();
    }
  }

  @Override
  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder) {
    if ("ide".equals(request.space) || "wildcard".equals(request.space) || "billing".equals(request.space)) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_DOCUMENT_SPACE_RESERVED));
      return;
    }
    nexus.adama.create(request.who, request.space, request.key, fixEntropy(request.entropy), request.arg.toString(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        responder.complete();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, MessageDirectSendRequest request, SeqResponder responder) {
    nexus.adama.directSend(request.who, request.space, request.key, null, request.channel, request.message.toString(), new Callback<Integer>() {
      @Override
      public void success(Integer seq) {
        responder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, MessageDirectSendOnceRequest request, SeqResponder responder) {
    nexus.adama.directSend(request.who, request.space, request.key, request.dedupe, request.channel, request.message.toString(), new Callback<Integer>() {
      @Override
      public void success(Integer seq) {
        responder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  private boolean validateDomain(DomainWithPolicy resolvedDomain, JsonResponder responder) {
    if (resolvedDomain.domain == null || resolvedDomain.policy == null) {
      responder.error(new ErrorCodeException(ErrorCodes.API_VALIDATE_DOMAIN_NO_SPACE_FOUND));
      return false;
    }
    if (resolvedDomain.domain.key == null) {
      responder.error(new ErrorCodeException(ErrorCodes.API_VALIDATE_DOMAIN_NO_KEY_FOUND));
      return false;
    }
    return true;
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateViaDomainRequest request, DataResponder responder) {
    if (validateDomain(request.resolvedDomain, responder.responder)) {
      return handle(session, new ConnectionCreateRequest(request.identity, request.who, request.resolvedDomain.domain.space, request.resolvedDomain.policy, request.resolvedDomain.domain.key, request.viewerState), responder);
    }
    return null;
  }

  @Override
  public DocumentStreamHandler handle(Session session, BillingConnectionCreateRequest request, DataResponder responder) {
    if (request.who.isAdamaDeveloper) {
      return handle(session, new ConnectionCreateRequest(request.identity, request.who, "billing", null, "" + request.who.id, Json.newJsonObject()), responder);
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.API_CONNECT_BILING_MUST_BE_ADAMA_DEVELOPER));
      return null;
    }
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest connect, DataResponder responder) {
    return new DocumentStreamHandler() {
      private AdamaStream connection;

      @Override
      public void bind() {
        connection = nexus.adama.connect(connect.who, connect.space, connect.key, connect.viewerState != null ? connect.viewerState.toString() : "{}", new SimpleEvents() {
          @Override
          public void connected() {
          }

          @Override
          public void delta(String data) {
            ObjectNode delta = Json.parseJsonObject(data);
            responder.next(delta);
            JsonNode force = delta.get("force-disconnect");
            if (force != null && force.isBoolean() && force.booleanValue()) {
              responder.error(new ErrorCodeException(ErrorCodes.AUTH_DISCONNECTED));
              connection.close();
            }
          }

          @Override
          public void traffic(String trafficHint) {
            // TODO: figure out something neat here
          }

          @Override
          public void error(int code) {
            responder.error(new ErrorCodeException(code));
          }

          @Override
          public void disconnected() {
            responder.finish();
          }
        });
      }

      @Override
      public void handle(ConnectionUpdateRequest request, SimpleResponder responder) {
        connection.update(request.viewerState != null ? request.viewerState.toString() : "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            responder.complete();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex);
          }
        });
      }

      @Override
      public void handle(ConnectionCanAttachRequest request, YesResponder responder) {
        connection.canAttach(new Callback<Boolean>() {
          @Override
          public void success(Boolean value) {
            responder.complete(value);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex);
          }
        });
      }

      @Override
      public void handle(ConnectionAttachRequest request, SeqResponder responder) {
        connection.attach(request.assetId, request.filename, request.contentType, request.size, request.digestMd5, request.digestSha384, WRAP(responder));
      }

      @Override
      public void handle(ConnectionSendOnceRequest request, SeqResponder responder) {
        connection.send(request.channel, request.dedupe, request.message.toString(), WRAP(responder));
      }

      @Override
      public void handle(ConnectionSendRequest request, SeqResponder responder) {
        connection.send(request.channel, null, request.message.toString(), WRAP(responder));
      }

      @Override
      public void handle(ConnectionPasswordRequest request, SimpleResponder responder) {
        nexus.adama.authorize(session.authenticator.getTransportContext().remoteIp, session.authenticator.getTransportContext().origin, connect.space, connect.key, request.username, request.password, request.new_password, new Callback<String>() {
          @Override
          public void success(String identity) {
            responder.complete();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex);
          }
        });
      }

      @Override
      public void handle(ConnectionEndRequest request, SimpleResponder responder) {
        connection.close();
        responder.complete();
      }

      @Override
      public void disconnect(long id) {
        connection.close();
      }

      @Override
      public void logInto(ObjectNode node) {
        connect.logInto(node);
      }
    };
  }

  @Override
  public AttachmentUploadHandler handle(Session session, AttachmentStartByDomainRequest request, ProgressResponder responder) {
    if (validateDomain(request.resolvedDomain, responder.responder)) {
      handle(session, new AttachmentStartRequest(request.identity, request.who, request.resolvedDomain.domain.space, request.resolvedDomain.policy, request.resolvedDomain.domain.key, request.filename, request.contentType), responder);
    }
    return null;
  }

  @Override
  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, ProgressResponder startResponder) {
    AtomicBoolean clean = new AtomicBoolean(false);
    AtomicBoolean killed = new AtomicBoolean(false);
    AtomicReference<AdamaStream> connection = new AtomicReference<>(null);
    Runnable kickOff = () -> {
      connection.set(nexus.adama.connect(request.who, request.space, request.key, "{}", new SimpleEvents() {
        @Override
        public void connected() {
        }

        @Override
        public void delta(String data) {
          // don't care for right now
        }

        @Override
        public void error(int code) {
          if (!killed.get()) {
            killed.set(true);
            startResponder.error(new ErrorCodeException(code));
          }
        }

        @Override
        public void traffic(String trafficHint) {
        }

        @Override
        public void disconnected() {
          if (!clean.get()) {
            if (!killed.get()) {
              killed.set(true);
              startResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_ATTACHMENT_LOST_CONNECTION));
            }
          } else {
            startResponder.finish();
          }
        }
      }));

      connection.get().canAttach(new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {
          if (value) {
            startResponder.next(65536);
          } else {
            if (!killed.get()) {
              killed.set(true);
              startResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_ATTACHMENT_NOT_ALLOWED));
            }
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          killed.set(true);
          startResponder.error(ex);
        }
      });
    };

    return new AttachmentUploadHandler() {
      String id;
      FileOutputStream output;
      File file;
      MessageDigest digestMD5;
      MessageDigest digestSHA384;
      int size;

      @Override
      public void bind() {
        try {
          id = ProtectedUUID.generate();
          file = new File(nexus.attachmentRoot, id + ".upload");
          file.deleteOnExit();
          digestMD5 = MessageDigest.getInstance("MD5");
          digestSHA384 = MessageDigest.getInstance("SHA-384");
          output = new FileOutputStream(file);
          size = 0;
          kickOff.run();
        } catch (Exception ex) {
          startResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_FAILED_BIND, ex, LOGGER));
        }
      }

      @Override
      public void handle(AttachmentAppendRequest attachChunk, SimpleResponder chunkResponder) {
        try {
          byte[] chunk = Base64.getDecoder().decode(attachChunk.base64Bytes);
          size += chunk.length;
          output.write(chunk);
          digestMD5.update(chunk);
          digestSHA384.update(chunk);
          boolean hasChunkMD5 = attachChunk.chunkMd5 != null && !("n/a".equalsIgnoreCase(attachChunk.chunkMd5) || "".equalsIgnoreCase(attachChunk.chunkMd5));
          if (hasChunkMD5) {
            MessageDigest chunkDigest = Hashing.md5();
            chunkDigest.update(chunk);
            if (!Hashing.finishAndEncode(chunkDigest).equals(attachChunk.chunkMd5)) {
              chunkResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_CHUNK_BAD_DIGEST));
              output.close();
              disconnect(0);
            } else {
              chunkResponder.complete();
              startResponder.next(65536);
            }
          } else {
            chunkResponder.complete();
            startResponder.next(65536);
          }
        } catch (Exception ex) {
          chunkResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_CHUNK_UNKNOWN_EXCEPTION, ex, LOGGER));
          disconnect(0);
        }
      }

      @Override
      public void handle(AttachmentFinishRequest attachFinish, AssetIdResponder finishResponder) {
        try {
          output.flush();
          output.close();
          String md5_64 = Hashing.finishAndEncode(digestMD5);
          String sha384_64 = Hashing.finishAndEncode(digestSHA384);
          NtAsset asset = new NtAsset(id, request.filename, request.contentType, this.size, md5_64, sha384_64);
          nexus.assets.upload(new Key(request.space, request.key), asset, AssetUploadBody.WRAP(file), new Callback<Void>() {
            @Override
            public void success(Void value) {
              clean.set(true);
              connection.get().attach(id, asset.name, asset.contentType, asset.size, asset.md5, asset.sha384, new Callback<Integer>() {
                @Override
                public void success(Integer value) {
                  disconnect(0L);
                  finishResponder.complete(id);
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  if (!killed.get()) {
                    killed.set(true);
                    startResponder.error(ex);
                  }
                  disconnect(0L);
                  finishResponder.error(ex);
                }
              });
            }

            @Override
            public void failure(ErrorCodeException ex) {
              if (!killed.get()) {
                killed.set(true);
                startResponder.error(ex);
              }
              disconnect(0L);
              finishResponder.error(ex);
            }
          });
        } catch (Exception ex) {
          finishResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_ATTACHMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
          disconnect(0);
        }
      }

      @Override
      public void disconnect(long id) {
        file.delete();
        connection.get().close();
      }

      @Override
      public void logInto(ObjectNode node) {
        request.logInto(node);
      }
    };
  }

  private void findBillableTarget(Session session, AuthenticatedUser who, Callback<Integer> callback) {
    if (who.isAdamaDeveloper) {
      callback.success(who.id);
      return;
    }
    // TODO(1.a): (parse authority to find doc/$space/$key) -> $space
    // TODO(1.b): (parse origin to find $domain, translate $domain to $space -> $space
    // TODO(2): convert $space to $owner
    callback.success(0);
  }

  @Override
  public void handle(Session session, FeatureSummarizeUrlRequest request, SummaryResponder responder) {
    findBillableTarget(session, request.who, new Callback<Integer>() {
      @Override
      public void success(Integer billTo) {
        UrlSummaryGenerator.summarize(nexus.webBase, request.url, new Callback<>() {
          @Override
          public void success(ObjectNode summary) {
            responder.complete(summary);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DocumentForceBackupRequest request, BackupItemSoloResponder responder) {
    nexus.adama.forceBackup(request.who, request.space, request.key, new Callback<String>() {
      @Override
      public void success(String backupId) {
        responder.complete(backupId);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void disconnect() {
  }

  private ReplicationStreamHandler replicateSetup(String space, String key, String nameOrMachine, ReplicaResponder responder) {
    DataObserver observer = new DataObserver() {
      @Override
      public String machine() {
        return nameOrMachine;
      }

      @Override
      public void start(String snapshot) {
        responder.next(true, Json.parseJsonObject(snapshot));

      }

      @Override
      public void change(String delta) {
        responder.next(false, Json.parseJsonObject(delta));
      }

      @Override
      public void failure(ErrorCodeException exception) {
        responder.error(exception);
      }
    };
    return new ReplicationStreamHandler() {
      private boolean cancelled = false;
      private Runnable storedCancel = null;
      synchronized Runnable set(Runnable cancel) {
        if (cancelled) {
          return cancel;
        }
        storedCancel = cancel;
        return null;
      }
      synchronized Runnable getCancelIfApplicable() {
        cancelled = true;
        Runnable result = storedCancel;
        storedCancel = null;
        return result;
      }
      void end() {
        Runnable toRun = getCancelIfApplicable();
        if (toRun != null) {
          toRun.run();
        }
      }
      @Override
      public void bind() {
        nexus.adama.startDocumentReplication(new Key(space, key), observer, new Callback<Runnable>() {
          @Override
          public void success(Runnable cancel) {
            Runnable rejected = set(cancel);
            if (rejected != null) {
              rejected.run();
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            // responder already relayed error
          }
        });
      }

      @Override
      public void handle(ReplicationEndRequest request, SimpleResponder endResponder) {
        end();
        endResponder.complete();
      }

      @Override
      public void logInto(ObjectNode node) {

      }

      @Override
      public void disconnect(long id) {
        end();
        responder.finish();
      }
    };
  }

  @Override
  public ReplicationStreamHandler handle(Session session, ReplicationCreateRequest request, ReplicaResponder responder) {
    return replicateSetup(request.space, request.key, "user-" + request.who, responder);
  }

  @Override
  public ReplicationStreamHandler handle(Session session, RegionalReplicationCreateRequest request, ReplicaResponder responder) {
    return replicateSetup(request.space, request.key, request.machine, responder);
  }

  private static Callback<Integer> WRAP(SeqResponder seqResponder) {
    return new Callback<>() {
      @Override
      public void success(Integer seq) {
        seqResponder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        seqResponder.error(ex);
      }
    };
  }
}
