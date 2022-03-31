/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.frontend;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.backend.BackendOperations;
import org.adamalang.mysql.backend.data.DocumentIndex;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.mysql.frontend.Authorities;
import org.adamalang.mysql.frontend.Billing;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.mysql.frontend.data.BillingUsage;
import org.adamalang.mysql.frontend.data.IdHashPairing;
import org.adamalang.mysql.frontend.data.Role;
import org.adamalang.mysql.frontend.data.SpaceListingItem;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RootHandlerImpl implements RootHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RootHandlerImpl.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(RootHandlerImpl.class);
  private final ExternNexus nexus;
  private final Random rng;

  public RootHandlerImpl(ExternNexus nexus) throws Exception {
    this.nexus = nexus;
    this.rng = new Random();
  }

  @Override
  public void handle(Session session, ConfigureMakeOrGetAssetKeyRequest request, AssetKeyResponder responder) {
    String assetKey = session.getAssetKey();
    if (assetKey == null) {
      assetKey = SecureAssetUtil.makeAssetKeyHeader();
      session.setAssetKey(assetKey);
    }
    responder.complete(assetKey);
  }

  @Override
  public void handle(Session session, AccountSetPasswordRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        String hash = SCryptUtil.scrypt(request.password, 16384, 8, 1);
        Users.setPasswordHash(nexus.dataBaseManagement, request.who.id, hash);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_PASSWORD_ONLY_ADAMA_DEV_EXCEPTION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_PASSWORD_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AccountLoginRequest request, InitiationResponder responder) {
    try {
      String hash = Users.getPasswordHash(nexus.dataBaseManagement, request.userId);
      if (SCryptUtil.check(request.password, hash)) {
        KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
        String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
        Users.addKey(nexus.dataBaseManagement, request.userId, publicKey, System.currentTimeMillis() + 14 * 24 * 60 * 60000);
        responder.complete(Jwts.builder().setSubject("" + request.userId).setIssuer("adama").signWith(pair.getPrivate()).compact());
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_PASSWORD_INVALID));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LOGIN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder) {
    try {
      String generatedCode = generateCode();
      String hash = SCryptUtil.scrypt(generatedCode, 16384, 8, 1);
      Users.addInitiationPair(nexus.dataBaseManagement, request.userId, hash, System.currentTimeMillis() + 15 * 60000);
      nexus.email.sendCode(request.email, generatedCode);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_SETUP_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder) {
    try {
      for (IdHashPairing idHash : Users.listInitiationPairs(nexus.dataBaseManagement, request.userId)) {
        if (SCryptUtil.check(request.code, idHash.hash)) {
          KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
          String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
          if (request.revoke != null && request.revoke) {
            Users.removeAllKeys(nexus.dataBaseManagement, request.userId);
          }
          Users.addKey(nexus.dataBaseManagement, request.userId, publicKey, System.currentTimeMillis() + 14 * 24 * 60 * 60000);
          responder.complete(Jwts.builder().setSubject("" + request.userId).setIssuer("adama").signWith(pair.getPrivate()).compact());
          Users.validateUser(nexus.dataBaseManagement, request.userId);
          Users.deleteInitiationPairing(nexus.dataBaseManagement, idHash.id);
          return;
        }
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_INIT_COMPLETE_CODE_MISMATCH));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_COMPLETE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private String generateCode() {
    StringBuilder code = new StringBuilder();
    for (int j = 0; j < 2; j++) {
      int val = rng.nextInt(26 * 26 * 26 * 26);
      for (int k = 0; k < 4; k++) {
        code.append(('A' + (val % 26)));
        val /= 26;
      }
    }
    return code.toString();
  }

  @Override
  public void handle(Session session, ProbeRequest request, SimpleResponder responder) {
    responder.complete();
  }

  @Override
  public void handle(Session session, AuthorityCreateRequest request, ClaimResultResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        String authority = ProtectedUUID.generate();
        Authorities.createAuthority(nexus.dataBaseManagement, request.who.id, authority);
        responder.complete(authority);
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: setKeystore validates ownership
        Authorities.setKeystore(nexus.dataBaseManagement, request.who.id, request.authority, request.keyStore.toString());
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: getKeystorePublic validates ownership
        responder.complete(Json.parseJsonObject(Authorities.getKeystorePublic(nexus.dataBaseManagement, request.who.id, request.authority)));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_GET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityListRequest request, AuthorityListingResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        for (String authority : Authorities.list(nexus.dataBaseManagement, request.who.id)) {
          responder.next(authority);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_LIST_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityDestroyRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: deleteAuthority validates ownership
        Authorities.deleteAuthority(nexus.dataBaseManagement, request.who.id, request.authority);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DELETE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder) {
    try {
      Spaces.createSpace(nexus.dataBaseManagement, request.who.id, request.space);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceUsageRequest request, BillingUsageResponder responder) {
    try {
      if (request.policy.canUserGetBillingUsage(request.who)) {
        for (BillingUsage usage : Billing.usageReport(nexus.dataBaseManagement, request.policy.id, request.limit != null ? request.limit.intValue() : 336)) {
          responder.next(usage.hour, usage.cpu, usage.memory, usage.connections, usage.documents, usage.messages, usage.storageBytes);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_GET_BILLING_USAGE_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_BILLING_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetRequest request, PlanResponder responder) {
    try {
      if (request.policy.canUserGetPlan(request.who)) {
        responder.complete(Json.parseJsonObject(Spaces.getPlan(nexus.dataBaseManagement, request.policy.id)));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_GET_PLAN_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserSetPlan(request.who)) {
        String planJson = request.plan.toString();
        // hash the plan
        MessageDigest digest = Hashing.md5();
        digest.digest(planJson.getBytes(StandardCharsets.UTF_8));
        String hash = Hashing.finishAndEncode(digest);
        // Change the master plan
        Spaces.setPlan(nexus.dataBaseManagement, request.policy.id, planJson, hash);
        // iterate the targets with this space loaded
        nexus.client.getDeploymentTargets(request.space, (target) -> {
          try {
            // persist the deployment binding
            Deployments.deploy(nexus.dataBaseDeployments, request.space, target, hash, planJson);
            // notify the client of an update
            nexus.client.notifyDeployment(target, request.space);
          } catch (Exception ex) {
            LOG.error("failed-deployment-write", ex);
          }
        });
        nexus.client.waitForCapacity(request.space, 7500, (found) -> {
          if (found) {
            responder.complete();
          } else {
            responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_SET_PLAN_DEPLOYMENT_FAILED_FINDING_CAPACITY));
          }
        });
      } else {
        throw new ErrorCodeException(ErrorCodes.API_SPACE_SET_PLAN_NO_PERMISSION_TO_EXECUTE);
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserDeleteSpace(request.who)) {
        if (BackendOperations.list(nexus.dataBaseBackend, request.space, null, 1).size() > 0) {
          responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_DELETE_NOT_EMPTY));
          return;
        }
        Spaces.changePrimaryOwner(nexus.dataBaseBackend, request.policy.id, request.policy.owner, 0);
        // remove all machines handling this
        Deployments.undeployAll(nexus.dataBaseDeployments, request.space);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_DELETE_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_DELETE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder) {
    try {
      Role role = Role.from(request.role);
      if (request.policy.canUserSetRole(request.who)) {
        Spaces.setRole(nexus.dataBaseManagement, request.policy.id, request.userId, role);
        responder.complete();
      } else {
        throw new ErrorCodeException(ErrorCodes.API_SPACE_SET_ROLE_NO_PERMISSION_TO_EXECUTE);
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder) {
    if (request.policy.canUserSeeReflection(request.who)) {
      nexus.client.reflect(request.space, request.key, new Callback<String>() {
        @Override
        public void success(String value) {
          responder.complete(Json.parseJsonObject(value));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_REFLECT_NO_PERMISSION_TO_EXECUTE));
    }
  }

  @Override
  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        for (SpaceListingItem spaceListingItem : Spaces.list(nexus.dataBaseManagement, request.who.id, request.marker, request.limit == null ? 100 : request.limit)) {
          responder.next(spaceListingItem.name, spaceListingItem.callerRole, spaceListingItem.created, spaceListingItem.enabled, spaceListingItem.storageBytes);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_LIST_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder) {
    try {
      nexus.client.create(session.context.remoteIp, session.context.origin, request.who.who.agent, request.who.who.authority, request.space, request.key, request.entropy, request.arg.toString(), new Callback<Void>() {
        @Override
        public void success(Void value) {
          responder.complete();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_DOCUMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder) {
    try {
      if (request.policy.canUserSeeKeyListing(request.who)) {
        for (DocumentIndex item : BackendOperations.list(nexus.dataBaseBackend, request.space, request.marker, request.limit != null ? request.limit : 100)) {
          responder.next(item.key, item.created, item.updated, item.seq);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_LIST_DOCUMENTS_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_DOCUMENTS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest request, DataResponder responder) {
    return new DocumentStreamHandler() {
      private Connection connection;

      @Override
      public void bind() {
        connection = nexus.client.connect("ip", "origin", request.who.who.agent, request.who.who.authority, request.space, request.key, request.viewerState != null ? request.viewerState.toString() : "{}", session.getAssetKey(), new SimpleEvents() {
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
      public void handle(ConnectionSendRequest request, SeqResponder responder) {
        connection.send(request.channel, null, request.message.toString(), new Callback<Integer>() {
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
        request.logInto(node);
      }
    };
  }

  @Override
  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, ProgressResponder startResponder) {
    AtomicReference<Connection> connection = new AtomicReference<>(null);
    AtomicBoolean clean = new AtomicBoolean(false);
    AtomicBoolean killed = new AtomicBoolean(false);
    Runnable kickOff = () -> {
      connection.set(nexus.client.connect(session.context.remoteIp, session.context.origin, request.who.who.agent, request.who.who.authority, request.space, request.key, "{}", session.getAssetKey(), new SimpleEvents() {
        @Override
        public void connected() {
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
          id = UUID.randomUUID().toString();
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
      public void handle(AttachmentFinishRequest attachFinish, SimpleResponder finishResponder) {
        try {
          output.flush();
          output.close();
          String md5_64 = Hashing.finishAndEncode(digestMD5);
          String sha384_64 = Hashing.finishAndEncode(digestSHA384);
          NtAsset asset = new NtAsset(id, request.filename, request.contentType, this.size, md5_64, sha384_64);
          nexus.uploader.upload(new Key(request.space, request.key), asset, file, new Callback<Void>() {
            @Override
            public void success(Void value) {
              connection.get().attach(id, asset.name, asset.contentType, asset.size, asset.md5, asset.sha384, new Callback<Integer>() {
                @Override
                public void success(Integer value) {
                  clean.set(true);
                  disconnect(0L);
                  finishResponder.complete();
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  disconnect(0L);
                  finishResponder.error(ex);
                }
              });
            }

            @Override
            public void failure(ErrorCodeException ex) {
              disconnect(0L);
              finishResponder.error(ex);
            }
          });
        } catch (Exception ex) {
          finishResponder.error(ErrorCodeException.detectOrWrap(0, ex, LOGGER));
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
}
