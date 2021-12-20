package org.adamalang.frontend;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.api.*;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

public class RootHandlerImpl implements RootHandler {
    private final ExternNexus nexus;
    private final SecureRandom rng;

    public RootHandlerImpl(ExternNexus nexus) throws Exception {
        this.nexus = nexus;
        this.rng = SecureRandom.getInstanceStrong();
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
    public WaitingForEmailHandler handle(InitStartRequest startRequest, SimpleResponder startResponder) {
        String generatedCode = "CODE"; // TODO abstract this out for testing
        // nexus.email.sendCode(request.email, generatedCode);

        // 1 GENERATE PROPER CODE
        // 2 SEND EMAIL ABOUT CODE
        return new WaitingForEmailHandler() {
            @Override
            public void handle(InitGenerateNewKeyPairRequest request, PrivateKeyResponder responder) {
                if (generatedCode.equals(request.code)) {
                    KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
                    String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
                    try {
                        Users.addKey(nexus.base, startRequest.userId, publicKey, new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60));
                    } catch (Exception ex) {
                        // TODO: better error code
                        responder.error(ErrorCodeException.detectOrWrap(2345, ex));
                        startResponder.error(ErrorCodeException.detectOrWrap(2345, ex));
                        return;
                    }
                    responder.complete(Jwts.builder().setSubject("" + startRequest.userId).setIssuer("adama").signWith(pair.getPrivate()).compact());
                    startResponder.complete();
                } else {
                    // TODO: better error code
                    responder.error(new ErrorCodeException(1));
                    startResponder.error(new ErrorCodeException(1));
                }
            }

            @Override
            public void handle(InitRevokeAllRequest request, SimpleResponder responder) {
                if (generatedCode.equals(request.code)) {
                    // 1: DELETE all held public keys
                    try {
                        Users.removeAllKeys(nexus.base, startRequest.userId);
                    } catch (Exception ex) {
                        // TODO: better error code
                        responder.error(ErrorCodeException.detectOrWrap(2345, ex));
                        startResponder.error(ErrorCodeException.detectOrWrap(2345, ex));
                        return;
                    }
                    responder.complete();
                } else {
                    responder.error(new ErrorCodeException(1));
                }
            }

            @Override
            public void disconnect(long id) {
            }
        };
    }

    @Override
    public void handle(BillingAddRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(BillingListRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(BillingGetRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceBillingSetRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(AuthorityClaimRequest request, ClaimResultResponder responder) {

    }

    @Override
    public void handle(AuthorityTransferOwnershipRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(AuthorityListRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(AuthorityKeysAddRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(AuthorityKeysListRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(AuthorityKeysRemoveRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceCreateRequest request, SimpleResponder responder) {
        System.err.println("creating space:" + request.space);
        responder.complete();
    }

    @Override
    public void handle(SpaceGetRequest request, PlanResponder responder) {
        System.err.println("get space space:" + request.space);
        responder.error(new ErrorCodeException(134));
    }

    @Override
    public void handle(SpaceUpdateRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceDeleteRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceRoleSetRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceOwnerSetRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceReflectRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(SpaceListRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(DocumentCreateRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(DocumentListRequest request, SimpleResponder responder) {

    }

    @Override
    public DocumentStreamHandler handle(ConnectionCreateRequest request, DataResponder responder) {
        return null;
    }

    @Override
    public void handle(WebHookAddRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(WebHookListRequest request, SimpleResponder responder) {

    }

    @Override
    public void handle(WebHookRemoveRequest request, SimpleResponder responder) {

    }

    @Override
    public AttachmentUploadHandler handle(AttachmentStartRequest request, SimpleResponder responder) {
        return null;
    }

    @Override
    public void disconnect() {

    }
}
