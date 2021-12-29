/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api;


public interface RootHandler {
  public WaitingForEmailHandler handle(InitStartRequest request, SimpleResponder responder);

  public void handle(ProbeRequest request, SimpleResponder responder);

  public void handle(AuthorityCreateRequest request, ClaimResultResponder responder);

  public void handle(AuthoritySetRequest request, SimpleResponder responder);

  public void handle(AuthorityTransferRequest request, SimpleResponder responder);

  public void handle(AuthorityListRequest request, AuthorityListingResponder responder);

  public void handle(AuthorityDestroyRequest request, SimpleResponder responder);

  public void handle(SpaceCreateRequest request, SimpleResponder responder);

  public void handle(SpaceGetRequest request, PlanResponder responder);

  public void handle(SpaceSetRequest request, SimpleResponder responder);

  public void handle(SpaceDeleteRequest request, SimpleResponder responder);

  public void handle(SpaceSetRoleRequest request, SimpleResponder responder);

  public void handle(SpaceOwnerSetRequest request, SimpleResponder responder);

  public void handle(SpaceReflectRequest request, ReflectionResponder responder);

  public void handle(SpaceListRequest request, SpaceListingResponder responder);

  public void handle(DocumentCreateRequest request, SimpleResponder responder);

  public void handle(DocumentListRequest request, SimpleResponder responder);

  public DocumentStreamHandler handle(ConnectionCreateRequest request, DataResponder responder);

  public AttachmentUploadHandler handle(AttachmentStartRequest request, SimpleResponder responder);

public void disconnect();

}
