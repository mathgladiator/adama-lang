/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import org.adamalang.frontend.Session;

public interface RootRegionHandler {
  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentAuthorizeDomainRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder);

  public void handle(Session session, DocumentDeleteRequest request, SimpleResponder responder);

  public void handle(Session session, MessageDirectSendRequest request, SeqResponder responder);

  public void handle(Session session, MessageDirectSendOnceRequest request, SeqResponder responder);

  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest request, DataResponder responder);

  public DocumentStreamHandler handle(Session session, ConnectionCreateViaDomainRequest request, DataResponder responder);

  public void handle(Session session, DocumentsHashPasswordRequest request, HashedPasswordResponder responder);

  public DocumentStreamHandler handle(Session session, BillingConnectionCreateRequest request, DataResponder responder);

  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, ProgressResponder responder);

  public AttachmentUploadHandler handle(Session session, AttachmentStartByDomainRequest request, ProgressResponder responder);

  public void disconnect();

  public static boolean test(String method) {
    switch (method) {
      case "document/authorize":
      case "document/authorize-domain":
      case "document/create":
      case "document/delete":
      case "message/direct-send":
      case "message/direct-send-once":
      case "connection/create":
      case "connection/create-via-domain":
      case "connection/send":
      case "connection/password":
      case "connection/send-once":
      case "connection/can-attach":
      case "connection/attach":
      case "connection/update":
      case "connection/end":
      case "documents/hash-password":
      case "billing-connection/create":
      case "attachment/start":
      case "attachment/start-by-domain":
      case "attachment/append":
      case "attachment/finish":
        return true;
      default:
        return false;
    }
  }
}
