package org.adamalang.frontend;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.frontend.Authorities;
import org.adamalang.mysql.frontend.Role;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.common.Json;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class RootHandlerImpl implements RootHandler {
    private final ExternNexus nexus;
    private final SecureRandom rng;
    private final ExceptionLogger logger;

    public RootHandlerImpl(ExternNexus nexus) throws Exception {
        this.nexus = nexus;
        this.rng = SecureRandom.getInstanceStrong();
        this.logger = nexus.makeLogger(RootHandler.class);
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
    public WaitingForEmailHandler handle(InitStartRequest startRequest, SimpleResponder startResponder) {
        String generatedCode = generateCode();
        return new WaitingForEmailHandler() {

            @Override
            public void bind() {
                nexus.email.sendCode(startRequest.email, generatedCode);
            }

            @Override
            public void handle(InitGenerateIdentityRequest request, InitiationResponder responder) {
                try {
                    if (generatedCode.equals(request.code)) {
                        KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
                        String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
                        try {
                            Users.addKey(nexus.dataBase, startRequest.userId, publicKey, new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60));
                        } catch (Exception ex) {
                            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_GENERATE_UNKNOWN_EXCEPTION, ex, logger));
                            return;
                        }
                        responder.complete(Jwts.builder().setSubject("" + startRequest.userId).setIssuer("adama").signWith(pair.getPrivate()).compact());
                    } else {
                        responder.error(new ErrorCodeException(ErrorCodes.API_INIT_GENERATE_CODE_MISMATCH));
                    }
                } finally {
                    startResponder.complete();
                }
            }

            @Override
            public void handle(InitRevokeAllRequest request, SimpleResponder responder) {
                if (generatedCode.equals(request.code)) {
                    try {
                        Users.removeAllKeys(nexus.dataBase, startRequest.userId);
                    } catch (Exception ex) {
                        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_REVOKE_ALL_UNKNOWN_EXCEPTION, ex, logger));
                        return;
                    }
                    responder.complete();
                } else {
                    responder.error(new ErrorCodeException(ErrorCodes.API_INIT_REVOKE_ALL_CODE_MISMATCH));
                }
            }

            @Override
            public void disconnect(long id) {
            }
        };
    }
    @Override
    public void handle(AuthorityCreateRequest request, ClaimResultResponder responder) {
        String authority = UUID.randomUUID().toString();
        try {
            if (request.who.source == AuthenticatedUser.Source.Adama) {
                Authorities.createAuthority(nexus.dataBase, request.who.id, authority);
                responder.complete(authority);
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(AuthoritySetRequest request, SimpleResponder responder) {
        try {
            if (request.who.source == AuthenticatedUser.Source.Adama) {
                // NOTE: setKeystore validates ownership
                Authorities.setKeystore(nexus.dataBase, request.who.id, request.authority, request.keyStore.toString());
                responder.complete();
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(AuthorityTransferRequest request, SimpleResponder responder) {
        try {
            if (request.who.source == AuthenticatedUser.Source.Adama) {
                // NOTE: changeOwner validates ownership
                Authorities.changeOwner(nexus.dataBase, request.authority, request.who.id, request.userId);
                responder.complete();
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_TRANSFER_OWNER_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_TRANSFER_OWNER_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(AuthorityListRequest request, AuthorityListingResponder responder) {
        try {
            if (request.who.source == AuthenticatedUser.Source.Adama) {
                for (String authority : Authorities.list(nexus.dataBase, request.who.id)) {
                    responder.next(authority);
                }
                responder.finish();
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_LIST_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(AuthorityDestroyRequest request, SimpleResponder responder) {
        try {
            if (request.who.source == AuthenticatedUser.Source.Adama) {
                // NOTE: deleteAuthority validates ownership
                Authorities.deleteAuthority(nexus.dataBase, request.who.id, request.authority);
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_DELETE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(SpaceCreateRequest request, SimpleResponder responder) {
        try {
            Spaces.createSpace(nexus.dataBase, request.who.id, request.space);
            responder.complete();
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(SpaceGetRequest request, PlanResponder responder) {
        try {
            if (request.policy.canUserGetPlan(request.who)) {
                responder.complete(Json.parseJsonObject(Spaces.getPlan(nexus.dataBase, request.policy.id)));
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_GET_PLAN_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(SpaceSetRequest request, SimpleResponder responder) {
        try {
            if (request.policy.canUserSetPlan(request.who)) {
                Spaces.setPlan(nexus.dataBase, request.policy.id, request.plan.toString());
                responder.complete();
            } else {
                throw new ErrorCodeException(ErrorCodes.API_SPACE_SET_PLAN_NO_PERMISSION_TO_EXECUTE);
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION, ex, logger));
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
                Spaces.setRole(nexus.dataBase, request.policy.id, request.userId, role);
                responder.complete();
            } else {
                throw new ErrorCodeException(ErrorCodes.API_SPACE_SET_ROLE_NO_PERMISSION_TO_EXECUTE);
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(SpaceOwnerSetRequest request, SimpleResponder responder) {
        try {
            if (request.policy.canUserChangeOwner(request.who)) {
                Spaces.changePrimaryOwner(nexus.dataBase, request.policy.id, request.policy.owner, request.userId);
                responder.complete();
            } else {
                throw new ErrorCodeException(ErrorCodes.API_SPACE_CHANGE_OWNER_NO_PERMISSION_TO_EXECUTE);
            }

        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CHANGE_OWNER_UNKNOWN_EXCEPTION, ex, logger));
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
                for (Spaces.Item item : Spaces.list(nexus.dataBase, request.who.id, request.marker, request.limit == null ? 100 : request.limit)) {
                    responder.next(item.name, item.callerRole, item.billing, item.created);
                }
                responder.finish();
            } else {
                responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_LIST_NO_PERMISSION_TO_EXECUTE));
            }
        } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    @Override
    public void handle(DocumentCreateRequest request, SimpleResponder responder) {
        // TODO: find the appropriate Adama host
        // TODO: send the create over and respond
    }

    @Override
    public void handle(DocumentListRequest request, SimpleResponder responder) {
        // TODO: find any appropriate Adama host (pick any, but this does depend on the underlying data model)
        // TODO: execute the list
    }

    @Override
    public DocumentStreamHandler handle(ConnectionCreateRequest request, DataResponder responder) {
        // TODO: find the _right_ adama host
        // TODO: execute the connect
        return null;
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
    public void disconnect() {

    }
}
