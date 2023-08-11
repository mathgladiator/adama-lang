/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.frontend.global;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.frontend.Session;
import org.adamalang.mysql.data.*;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.mysql.model.*;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetUploadBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalDataHandler implements RootRegionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalDataHandler.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
  private final GlobalExternNexus nexus;
  private final Random rng;

  public GlobalDataHandler(GlobalExternNexus nexus) throws Exception {
    this.nexus = nexus;
    this.rng = new Random();
  }

  private void commonAuthorize(Session session, Key key, String username, String password, InitiationResponder responder) {
    nexus.adama.authorize(session.authenticator.ip(), session.authenticator.origin(), key.space, key.key, username, password, new Callback<String>() {
      @Override
      public void success(String agent) {
        try {
          String identity = Secrets.getOrCreateDocumentSigningKey(nexus.database, nexus.masterKey, key.space, key.key).signDocument(key.space, key.key, agent);
          responder.complete(identity);
        } catch (Exception ex) {
          responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_AUTH_DOCUMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder) {
    commonAuthorize(session, new Key(request.space, request.key), request.username, request.password, responder);
  }

  @Override
  public void handle(Session session, DocumentAuthorizeDomainRequest request, InitiationResponder responder) {
    try {
      Domain domain = Domains.get(nexus.database, request.domain);
      if (domain != null) {
        if (domain.key != null) {
          commonAuthorize(session, new Key(domain.space, domain.key), request.username, request.password, responder);
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_DOMAIN_AUTH_NO_KEY_MAPPED));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_DOMAIN_AUTH_DOMAIN_INVALID_MAPPED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_AUTH_DOMAIN_AUTH_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentsHashPasswordRequest request, HashedPasswordResponder responder) {
    responder.complete(SCryptUtil.scrypt(request.password, 16384, 8, 1));
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

  @Override
  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder) {
    if ("ide".equals(request.space) || "wildcard".equals(request.space) || "billing".equals(request.space)) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_DOCUMENT_SPACE_RESERVED));
      return;
    }
    nexus.adama.create(request.who, request.space, request.key, request.entropy, request.arg.toString(), new Callback<Void>() {
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

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateViaDomainRequest request, DataResponder responder) {
    if (request.resolvedDomain.domain == null || request.resolvedDomain.policy == null) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CONNECT_DOMAIN_NO_SPACE_FOUND));
      return null;
    }
    if (request.resolvedDomain.domain.key == null) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CONNECT_DOMAIN_NO_KEY_FOUND));
      return null;
    }
    return handle(session, new ConnectionCreateRequest(request.identity, request.who, request.resolvedDomain.domain.space, request.resolvedDomain.policy, request.resolvedDomain.domain.key, request.viewerState), responder);
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
            responder.next(Json.parseJsonObject(data));
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
        connection.update(request.viewerState != null ? request.viewerState.toString() : "{}");
        responder.complete();
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
      public void handle(ConnectionPasswordRequest request, SeqResponder responder) {
        // public void authorize(String ip, String origin, String space, String key, String username, String password, Callback<String> callback) {
        nexus.adama.authorize(session.authenticator.ip(), session.authenticator.origin(), connect.space, connect.key, request.username, request.password, new Callback<String>() {
          @Override
          public void success(String identity) {
            connection.password(request.password, WRAP(responder));
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

  @Override
  public void disconnect() {
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
