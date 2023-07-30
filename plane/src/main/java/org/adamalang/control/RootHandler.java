/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

import org.adamalang.Session;

public interface RootHandler {
  public void handle(Session session, GlobalMachineStartRequest request, MachineStartResponder responder);

  public void handle(Session session, GlobalFinderFindRequest request, FoundResponder responder);

  public void handle(Session session, GlobalFinderFindbindRequest request, FoundResponder responder);

  public void handle(Session session, GlobalFinderFreeRequest request, VoidResponder responder);

  public void handle(Session session, GlobalFinderDeleteMarkRequest request, VoidResponder responder);

  public void handle(Session session, GlobalFinderDeleteCommitRequest request, VoidResponder responder);

  public void handle(Session session, GlobalFinderBackUpRequest request, VoidResponder responder);

  public void handle(Session session, GlobalFinderListRequest request, KeyidResponder responder);

  public void handle(Session session, GlobalAuthoritiesCreateRequest request, AuthorityResponder responder);

  public void handle(Session session, GlobalAuthoritiesSetRequest request, VoidResponder responder);

  public void handle(Session session, GlobalAuthoritiesGetPublicRequest request, KeystoreResponder responder);

  public void handle(Session session, GlobalAuthoritiesGetProtectedRequest request, KeystoreResponder responder);

  public void handle(Session session, GlobalAuthoritiesListRequest request, AuthorityListResponder responder);

  public void handle(Session session, GlobalAuthoritiesDeleteRequest request, VoidResponder responder);

  public void disconnect();

}
