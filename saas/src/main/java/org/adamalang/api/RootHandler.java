package org.adamalang.api;


public interface RootHandler {
  public WaitingForEmailHandler handle(InitStartRequest request, SimpleResponder responder);

  public void handle(ProbeRequest request, SimpleResponder responder);

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

  public void handle(SpaceSetRoleRequest request, SimpleResponder responder);

  public void handle(SpaceOwnerSetRequest request, SimpleResponder responder);

  public void handle(SpaceReflectRequest request, SimpleResponder responder);

  public void handle(SpaceListRequest request, SpaceListingResponder responder);

  public void handle(DocumentCreateRequest request, SimpleResponder responder);

  public void handle(DocumentListRequest request, SimpleResponder responder);

  public DocumentStreamHandler handle(ConnectionCreateRequest request, DataResponder responder);

  public AttachmentUploadHandler handle(AttachmentStartRequest request, SimpleResponder responder);

public void disconnect();

}
