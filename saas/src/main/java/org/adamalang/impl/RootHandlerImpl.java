package org.adamalang.impl;

import org.adamalang.api.*;
import org.adamalang.runtime.exceptions.ErrorCodeException;

public class RootHandlerImpl implements RootHandler {
    @Override
    public WaitingForEmailHandler handle(InitStartRequest request, SimpleResponder responder) {
        return null;
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
