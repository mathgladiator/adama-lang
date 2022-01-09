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
import org.adamalang.extern.ExternNexus;
import org.adamalang.grpc.client.contracts.CreateCallback;
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.adamalang.grpc.client.contracts.SimpleEvents;
import org.adamalang.grpc.client.sm.Connection;
import org.adamalang.mysql.backend.Deployments;
import org.adamalang.mysql.frontend.Authorities;
import org.adamalang.mysql.frontend.Role;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.transforms.results.AuthenticatedUser;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RootHandlerImpl implements RootHandler {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(RootHandlerImpl.class);
  private final ExternNexus nexus;
  private final Random rng;
  private final AtomicInteger validationClassId;

  public RootHandlerImpl(ExternNexus nexus) throws Exception {
    this.nexus = nexus;
    this.rng = new Random();
    validationClassId = new AtomicInteger(0);
  }

  @Override
  public WaitingForEmailHandler handle(
      InitStartRequest startRequest, SimpleResponder startResponder) {
    String generatedCode = generateCode();
    return new WaitingForEmailHandler() {

      @Override
      public void bind() {
        nexus.email.sendCode(startRequest.email, generatedCode);
      }

      @Override
      public void handle(InitRevokeAllRequest request, SimpleResponder responder) {
        if (generatedCode.equals(request.code)) {
          try {
            Users.removeAllKeys(nexus.dataBaseManagement, startRequest.userId);
            Users.validateUser(nexus.dataBaseManagement, startRequest.userId);
          } catch (Exception ex) {
            responder.error(
                ErrorCodeException.detectOrWrap(
                    ErrorCodes.API_INIT_REVOKE_ALL_UNKNOWN_EXCEPTION, ex, LOGGER));
            return;
          }
          responder.complete();
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_INIT_REVOKE_ALL_CODE_MISMATCH));
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
              Users.addKey(
                  nexus.dataBaseManagement,
                  startRequest.userId,
                  publicKey,
                  new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60));
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
  public void handle(ProbeRequest request, SimpleResponder responder) {
    responder.complete();
  }

  @Override
  public void handle(AuthorityCreateRequest request, ClaimResultResponder responder) {
    String authority = UUID.randomUUID().toString();
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
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
  public void handle(AuthoritySetRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: setKeystore validates ownership
        Authorities.setKeystore(nexus.dataBaseManagement, request.who.id, request.authority, request.keyStore.toString());
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
  public void handle(AuthorityGetRequest request, KeystoreResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: getKeystorePublic validates ownership
        responder.complete(Json.parseJsonObject(Authorities.getKeystorePublic(nexus.dataBaseManagement, request.who.id, request.authority)));
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
  public void handle(AuthorityListRequest request, AuthorityListingResponder responder) {
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
  public void handle(AuthorityDestroyRequest request, SimpleResponder responder) {
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
  public void handle(SpaceCreateRequest request, SimpleResponder responder) {
    try {
      if (Validators.simple(request.space, 127)) {
        Spaces.createSpace(nexus.dataBaseManagement, request.who.id, request.space);
        responder.complete();
      } else {
        responder.error(
            new ErrorCodeException(ErrorCodes.API_SPACE_CREATE_INVALID_NAME));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(SpaceGetRequest request, PlanResponder responder) {
    try {
      if (request.policy.canUserGetPlan(request.who)) {
        responder.complete(Json.parseJsonObject(Spaces.getPlan(nexus.dataBaseManagement, request.policy.id)));
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
  public void handle(SpaceSetRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserSetPlan(request.who)) {
        String planJson = request.plan.toString();
        // hash the plan
        MessageDigest digest = Hashing.md5();
        digest.digest(planJson.getBytes(StandardCharsets.UTF_8));
        String hash = Hashing.finishAndEncode(digest);
        // validate the plan
        DeploymentPlan localPlan = new DeploymentPlan(planJson, (t, c) -> t.printStackTrace());
        new DeploymentFactory(request.space, "Space_" + request.space, validationClassId, null, localPlan);
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
            ex.printStackTrace();
          }
        });
        responder.complete();
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
  public void handle(SpaceDeleteRequest request, SimpleResponder responder) {
    // TODO: see if the space is empty, if not then reject
    // TODO: this requires document listing to work which is tricky due to the bifurication
  }

  @Override
  public void handle(SpaceSetRoleRequest request, SimpleResponder responder) {
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
  public void handle(SpaceReflectRequest request, ReflectionResponder responder) {
    // TODO: find the space
    // TODO: go to the Adama host
    // TODO: ask the factory for the reflection string
  }

  @Override
  public void handle(SpaceListRequest request, SpaceListingResponder responder) {
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
  public void handle(DocumentCreateRequest request, SimpleResponder responder) {
    try {
      nexus.client.create(request.who.who.agent, request.who.who.authority, request.space, request.key, request.entropy, request.arg.toString(), new CreateCallback() {
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
          ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_DOCUMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(DocumentListRequest request, SimpleResponder responder) {
    // TODO: find any appropriate Adama host (pick any, but this does depend on the underlying data
    // model)
    // TODO: execute the list
  }

  @Override
  public DocumentStreamHandler handle(ConnectionCreateRequest request, DataResponder responder) {
    return new DocumentStreamHandler() {
      private Connection connection;
      @Override
      public void bind() {
        connection = nexus.client.connect(request.who.who.agent, request.who.who.authority, request.space, request.key, new SimpleEvents() {
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
      public void handle(ConnectionSendRequest request, SimpleResponder responder) {
        connection.send(request.channel, null, request.message.toString(), new SeqCallback() {
          @Override
          public void success(int seq) {
            responder.complete();
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
      }

      @Override
      public void disconnect(long id) {
        connection.close();
      }
    };
  }

  @Override
  public AttachmentUploadHandler handle(AttachmentStartRequest request, SimpleResponder responder) {
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
          file = new File(root,id + ".upload");
          file.deleteOnExit();
          digestMD5 = MessageDigest.getInstance("MD5");
          digestSHA384 = MessageDigest.getInstance("SHA-384");
          output = new FileOutputStream(file);
        } catch (Exception ex) {
          responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_FAILED_BIND, ex, LOGGER));
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
          chunkResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_CHUNK_UNKNOWN_EXCEPTION, ex, LOGGER));
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
