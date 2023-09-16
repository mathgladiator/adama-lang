/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.api;

import org.adamalang.frontend.Session;

public interface RootRegionHandler {
  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentAuthorizeDomainRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentAuthorizeWithResetRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentAuthorizeDomainWithResetRequest request, InitiationResponder responder);

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
      case "document/authorize-with-reset":
      case "document/authorize-domain-with-reset":
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
