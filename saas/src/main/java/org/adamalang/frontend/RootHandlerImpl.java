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
import org.adamalang.transforms.results.AuthenticatedUser;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;

public class RootHandlerImpl implements RootHandler {
  private final ExternNexus nexus;
  private final Random rng;
  private final ExceptionLogger logger;

  public RootHandlerImpl(ExternNexus nexus) throws Exception {
    this.nexus = nexus;
    this.rng = new Random(); // TODO: figure out why secure random was hanging
    this.logger = nexus.makeLogger(RootHandler.class);
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
          } catch (Exception ex) {
            responder.error(
                ErrorCodeException.detectOrWrap(
                    ErrorCodes.API_INIT_REVOKE_ALL_UNKNOWN_EXCEPTION, ex, logger));
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
            } catch (Exception ex) {
              responder.error(
                  ErrorCodeException.detectOrWrap(
                      ErrorCodes.API_INIT_GENERATE_UNKNOWN_EXCEPTION, ex, logger));
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
              ErrorCodes.API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
    }
  }

  @Override
  public void handle(AuthoritySetRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: setKeystore validates ownership
        Authorities.setKeystore(
            nexus.dataBaseManagement, request.who.id, request.authority, request.keyStore.toString());
        responder.complete();
      } else {
        responder.error(
            new ErrorCodeException(ErrorCodes.API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(
              ErrorCodes.API_SET_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
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
              ErrorCodes.API_LIST_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
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
              ErrorCodes.API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
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
              ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, logger));
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
              ErrorCodes.API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION, ex, logger));
    }
  }

  @Override
  public void handle(SpaceSetRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserSetPlan(request.who)) {
        // Change the master plan
        String planJson = request.plan.toString();
        MessageDigest digest = Hashing.md5();
        digest.digest(planJson.getBytes(StandardCharsets.UTF_8));
        String hash = Hashing.finishAndEncode(digest);
        Spaces.setPlan(nexus.dataBaseManagement, request.policy.id, planJson);
        nexus.client.getDeploymentTargets(request.space, (target) -> {
          System.err.println("Deploying " + request.space + " to " + target);
          try {
            Deployments.deploy(nexus.dataBaseDeployments, request.space, target, hash, planJson);
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
              ErrorCodes.API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION, ex, logger));
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
              ErrorCodes.API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION, ex, logger));
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
          ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_UNKNOWN_EXCEPTION, ex, logger));
    }
  }

  @Override
  public void handle(DocumentCreateRequest request, SimpleResponder responder) {
    try {
      if (Validators.simple(request.key, 511)) {
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
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_DOCUMENT_INVALID_KEY));
      }
    } catch (Exception ex) {
      responder.error(
          ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_DOCUMENT_UNKNOWN_EXCEPTION, ex, logger));
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
    // TODO: find the _right_ adama host
    // TODO: ask if the current user can attach
    return new AttachmentUploadHandler() {
      @Override
      public void bind() {
        // TODO: OPEN LOCAL FILE
        // TODO: OPEN DIGEST FOR MD5
        // TODO: OPEN DIGEST FOR SHA-384
        // TODO: generate ID
      }

      @Override
      public void handle(AttachmentAppendRequest request, SimpleResponder responder) {
        // TODO: UPDATE DIGESTS
        // TODO: VALIDATE CHUNK
        // TODO: APPEND TO FILE
      }

      @Override
      public void handle(AttachmentFinishRequest request, SimpleResponder responder) {
        // TODO: CLOSE THE FILE
        // TODO: COMPUTE FINAL DIGESTS
        // TODO: UPLOAD TO S3
        // TODO: find _right_ adama host
        // TODO: attach to Adama
        // TODO: DELETE FILE

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
