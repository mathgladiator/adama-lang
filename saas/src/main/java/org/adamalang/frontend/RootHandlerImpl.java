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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.extern.ProtectedUUID;
import org.adamalang.grpc.client.contracts.CreateCallback;
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.adamalang.grpc.client.contracts.SimpleEvents;
import org.adamalang.grpc.client.sm.Connection;
import org.adamalang.mysql.backend.BackendOperations;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.mysql.frontend.Authorities;
import org.adamalang.mysql.frontend.Role;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
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
import java.util.concurrent.atomic.AtomicInteger;

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
  public WaitingForEmailHandler handle(Session session, InitStartRequest startRequest, SimpleResponder startResponder) {
    String generatedCode = generateCode();
    return new WaitingForEmailHandler() {

      @Override
      public void bind() {
        nexus.email.sendCode(startRequest.email, generatedCode);
      }

      @Override
      public void handle(InitRevokeAllRequest request, SimpleResponder responder) {
        try {
          if (generatedCode.equals(request.code)) {
            try {
              Users.removeAllKeys(nexus.dataBaseManagement, startRequest.userId);
              Users.validateUser(nexus.dataBaseManagement, startRequest.userId);
              responder.complete();
            } catch (Exception ex) {
              responder.error(
                  ErrorCodeException.detectOrWrap(
                      ErrorCodes.API_INIT_REVOKE_ALL_UNKNOWN_EXCEPTION, ex, LOGGER));
              return;
            }
          } else {
            responder.error(new ErrorCodeException(ErrorCodes.API_INIT_REVOKE_ALL_CODE_MISMATCH));
          }
        } finally {
          startResponder.complete();
        }
      }

      @Override
      public void handle(InitGenerateIdentityRequest request, InitiationResponder responder) {
        try {
          if (generatedCode.equals(request.code)) {
            KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
            String publicKey =
                new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
            try {
              if (request.revoke != null && request.revoke) {
                Users.removeAllKeys(nexus.dataBaseManagement, startRequest.userId);
              }
              Users.addKey(
                  nexus.dataBaseManagement,
                  startRequest.userId,
                  publicKey,
                  System.currentTimeMillis() + 30 * 24 * 60 * 60);
              Users.validateUser(nexus.dataBaseManagement, startRequest.userId);
            } catch (Exception ex) {
              responder.error(
                  ErrorCodeException.detectOrWrap(
                      ErrorCodes.API_INIT_GENERATE_UNKNOWN_EXCEPTION, ex, LOGGER));
              return;
            }
            responder.complete(
                Jwts.builder()
                    .setSubject("" + startRequest.userId)
                    .setIssuer("adama")
                    .signWith(pair.getPrivate())
                    .compact());
          } else {
            responder.error(new ErrorCodeException(ErrorCodes.API_INIT_GENERATE_CODE_MISMATCH));
          }
        } finally {
          startResponder.complete();
        }
      }

      @Override
      public void disconnect(long id) {}
    };
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
        responder.error(
            new ErrorCodeException(ErrorCodes.API_CREATE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: setKeystore validates ownership
        Authorities.setKeystore(
            nexus.dataBaseManagement,
            request.who.id,
            request.authority,
            request.keyStore.toString());
        responder.complete();
      } else {
        responder.error(
            new ErrorCodeException(ErrorCodes.API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: getKeystorePublic validates ownership
        responder.complete(
            Json.parseJsonObject(
                Authorities.getKeystorePublic(
                    nexus.dataBaseManagement, request.who.id, request.authority)));
      } else {
        responder.error(
            new ErrorCodeException(ErrorCodes.API_GET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_GET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
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
        responder.error(
            new ErrorCodeException(ErrorCodes.API_LIST_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_LIST_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
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
        responder.error(
            new ErrorCodeException(ErrorCodes.API_DELETE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder) {
    try {
      Spaces.createSpace(nexus.dataBaseManagement, request.who.id, request.space);
      responder.complete();
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetRequest request, PlanResponder responder) {
    try {
      if (request.policy.canUserGetPlan(request.who)) {
        responder.complete(
            Json.parseJsonObject(Spaces.getPlan(nexus.dataBaseManagement, request.policy.id)));
      } else {
        responder.error(
            new ErrorCodeException(ErrorCodes.API_SPACE_GET_PLAN_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
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
        nexus.client.getDeploymentTargets(
            request.space,
            (target) -> {
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
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
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
        Spaces.changePrimaryOwner(
            nexus.dataBaseBackend, request.policy.id, request.policy.owner, 0);
        // remove all machines handling this
        Deployments.undeployAll(nexus.dataBaseDeployments, request.space);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_DELETE_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SPACE_DELETE_UNKNOWN_EXCEPTION, ex, LOGGER));
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
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder) {
    if (request.policy.canUserSeeReflection(request.who)) {
      nexus.client.reflect(
          request.space,
          request.key,
          new Callback<String>() {
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
      responder.error(
          new ErrorCodeException(ErrorCodes.API_SPACE_REFLECT_NO_PERMISSION_TO_EXECUTE));
    }
  }

  @Override
  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        for (Spaces.Item item :
            Spaces.list(
                nexus.dataBaseManagement,
                request.who.id,
                request.marker,
                request.limit == null ? 100 : request.limit)) {
          responder.next(item.name, item.callerRole, item.billing, item.created);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_LIST_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder) {
    try {
      nexus.client.create(
          request.who.who.agent,
          request.who.who.authority,
          request.space,
          request.key,
          request.entropy,
          request.arg.toString(),
          new CreateCallback() {
            @Override
            public void created() {
              responder.complete();
            }

            @Override
            public void error(int code) {
              responder.error(new ErrorCodeException(code));
            }
          });
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_CREATE_DOCUMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder) {
    try {
      if (request.policy.canUserSeeKeyListing(request.who)) {
        for (BackendOperations.DocumentIndex item :
            BackendOperations.list(
                nexus.dataBaseBackend, request.space, request.marker, request.limit != null ? request.limit : 100)) {
          responder.next(item.key, item.created, item.updated, item.seq);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_LIST_DOCUMENTS_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_LIST_DOCUMENTS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest request, DataResponder responder) {
    return new DocumentStreamHandler() {
      private Connection connection;

      @Override
      public void bind() {
        connection =
            nexus.client.connect(
                request.who.who.agent,
                request.who.who.authority,
                request.space,
                request.key,
                new SimpleEvents() {
                  @Override
                  public void connected() {}

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
      public void handle(ConnectionSendRequest request, SeqResponder responder) {
        connection.send(
            request.channel,
            null,
            request.message.toString(),
            new SeqCallback() {
              @Override
              public void success(int seq) {
                responder.complete(seq);
              }

              @Override
              public void error(int code) {
                responder.error(new ErrorCodeException(code));
              }
            });
      }

      // TODO: attach, canAttach
      @Override
      public void handle(ConnectionEndRequest request, SimpleResponder responder) {
        connection.close();
        responder.complete();
      }

      @Override
      public void disconnect(long id) {
        connection.close();
      }
    };
  }

  @Override
  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, SimpleResponder responder) {
    return new AttachmentUploadHandler() {
      String id;
      FileOutputStream output;
      File file;
      MessageDigest digestMD5;
      MessageDigest digestSHA384;

      @Override
      public void bind() {
        try {
          id = UUID.randomUUID().toString();
          File root = new File("inflight");
          file = new File(root, id + ".upload");
          file.deleteOnExit();
          digestMD5 = MessageDigest.getInstance("MD5");
          digestSHA384 = MessageDigest.getInstance("SHA-384");
          output = new FileOutputStream(file);
        } catch (Exception ex) {
          responder.error(
              ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_FAILED_BIND, ex, LOGGER));
        }
      }

      @Override
      public void handle(AttachmentAppendRequest request, SimpleResponder chunkResponder) {
        try {
          byte[] chunk = Base64.getDecoder().decode(request.base64Bytes);
          output.write(chunk);
          digestMD5.update(chunk);
          digestSHA384.update(chunk);
          MessageDigest chunkDigest = Hashing.md5();
          chunkDigest.update(chunk);
          if (!Hashing.finishAndEncode(chunkDigest).equals(request.chunkMd5)) {
            chunkResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_CHUNK_BAD_DIGEST));
          } else {
            chunkResponder.complete();
          }
        } catch (Exception ex) {
          chunkResponder.error(
              ErrorCodeException.detectOrWrap(
                  ErrorCodes.API_ASSET_CHUNK_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }

      @Override
      public void handle(AttachmentFinishRequest request, SimpleResponder responder) {
        try {
          output.flush();
          output.close();
          String md5_64 = Hashing.finishAndEncode(digestMD5);
          // TODO: convert this digest to an S3 compatible
          String sha384_64 = Hashing.finishAndEncode(digestSHA384);
          // TODO: UPLOAD TO S3
          // TODO: find _right_ adama host
          // TODO: attach to Adama
          file.delete();
        } catch (Exception ex) {

        }
      }

      @Override
      public void disconnect(long id) {
        // TODO: if finished, then nothing
        // TODO: otherwise, delete file
      }
    };
  }

  @Override
  public void disconnect() {}
}
