package org.adamalang.api;


public interface RootHandler {
  public WaitingForEmailHandler handle(InitStartRequest request, SimpleResponder responder);

  public void handle(ProbeRequest request, SimpleResponder responder);

  public void handle(BillingAddRequest request, SimpleResponder responder);

  public void handle(BillingListRequest request, SimpleResponder responder);

  public void handle(BillingGetRequest request, SimpleResponder responder);

  public void handle(SpaceBillingSetRequest request, SimpleResponder responder);

  public void handle(AuthorityClaimRequest request, ClaimResultResponder responder);

  public void handle(AuthorityTransferOwnershipRequest request, SimpleResponder responder);

  public void handle(AuthorityListRequest request, SimpleResponder responder);

  public void handle(AuthorityKeysAddRequest request, SimpleResponder responder);

  public void handle(AuthorityKeysListRequest request, SimpleResponder responder);

  public void handle(AuthorityKeysRemoveRequest request, SimpleResponder responder);

  public void handle(SpaceCreateRequest request, SimpleResponder responder);

  public void handle(SpaceGetRequest request, PlanResponder responder);

  public void handle(SpaceUpdateRequest request, SimpleResponder responder);

  public void handle(SpaceDeleteRequest request, SimpleResponder responder);

  public void handle(SpaceRoleSetRequest request, SimpleResponder responder);

  public void handle(SpaceOwnerSetRequest request, SimpleResponder responder);

  public void handle(SpaceReflectRequest request, SimpleResponder responder);

  public void handle(SpaceListRequest request, SimpleResponder responder);

  public void handle(DocumentCreateRequest request, SimpleResponder responder);

  public void handle(DocumentListRequest request, SimpleResponder responder);

  public DocumentStreamHandler handle(ConnectionCreateRequest request, DataResponder responder);

  public void handle(WebHookAddRequest request, SimpleResponder responder);

  public void handle(WebHookListRequest request, SimpleResponder responder);

  public void handle(WebHookRemoveRequest request, SimpleResponder responder);

  public AttachmentUploadHandler handle(AttachmentStartRequest request, SimpleResponder responder);

public void disconnect();

}
