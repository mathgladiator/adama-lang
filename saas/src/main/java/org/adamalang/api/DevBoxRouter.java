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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

public abstract class DevBoxRouter {

  public abstract void handle_SpaceReflect(long requestId, String identity, String space, String key, ReflectionResponder responder);

  public abstract void handle_PushRegister(long requestId, String identity, String domain, ObjectNode subscription, ObjectNode deviceInfo, SimpleResponder responder);

  public abstract void handle_DomainReflect(long requestId, String identity, String domain, ReflectionResponder responder);

  public abstract void handle_DomainGetVapidPublicKey(long requestId, String identity, String domain, DomainVapidResponder responder);

  public abstract void handle_DocumentAuthorize(long requestId, String space, String key, String username, String password, InitiationResponder responder);

  public abstract void handle_DocumentAuthorizeDomain(long requestId, String domain, String username, String password, InitiationResponder responder);

  public abstract void handle_DocumentAuthorizeWithReset(long requestId, String space, String key, String username, String password, String new_password, InitiationResponder responder);

  public abstract void handle_DocumentAuthorizeDomainWithReset(long requestId, String domain, String username, String password, String new_password, InitiationResponder responder);

  public abstract void handle_ConnectionCreate(long requestId, String identity, String space, String key, ObjectNode viewerState, DataResponder responder);

  public abstract void handle_ConnectionCreateViaDomain(long requestId, String identity, String domain, ObjectNode viewerState, DataResponder responder);

  public abstract void handle_ConnectionSend(long requestId, Long connection, String channel, JsonNode message, SeqResponder responder);

  public abstract void handle_ConnectionPassword(long requestId, Long connection, String username, String password, String new_password, SimpleResponder responder);

  public abstract void handle_ConnectionSendOnce(long requestId, Long connection, String channel, String dedupe, JsonNode message, SeqResponder responder);

  public abstract void handle_ConnectionCanAttach(long requestId, Long connection, YesResponder responder);

  public abstract void handle_ConnectionAttach(long requestId, Long connection, String assetId, String filename, String contentType, Long size, String digestMd5, String digestSha384, SeqResponder responder);

  public abstract void handle_ConnectionUpdate(long requestId, Long connection, ObjectNode viewerState, SimpleResponder responder);

  public abstract void handle_ConnectionEnd(long requestId, Long connection, SimpleResponder responder);

  public abstract void handle_DocumentsHashPassword(long requestId, String password, HashedPasswordResponder responder);

  public abstract void handle_ConfigureMakeOrGetAssetKey(long requestId, AssetKeyResponder responder);

  public void route(JsonRequest request, JsonResponder responder) {
    try {
      long requestId = request.id();
      String method = request.method();
      switch (method) {
        case "space/reflect":
          handle_SpaceReflect(requestId, //
          request.getString("identity", true, 458759), //
          request.getStringNormalize("space", true, 461828), //
          request.getString("key", true, 466947), //
          new ReflectionResponder(responder));
          return;
        case "push/register":
          handle_PushRegister(requestId, //
          request.getString("identity", true, 458759), //
          request.getString("domain", true, 488444), //
          request.getObject("subscription", true, 407308), //
          request.getObject("device-info", true, 446218), //
          new SimpleResponder(responder));
          return;
        case "domain/reflect":
          handle_DomainReflect(requestId, //
          request.getString("identity", true, 458759), //
          request.getString("domain", true, 488444), //
          new ReflectionResponder(responder));
          return;
        case "domain/get-vapid-public-key":
          handle_DomainGetVapidPublicKey(requestId, //
          request.getString("identity", true, 458759), //
          request.getString("domain", true, 488444), //
          new DomainVapidResponder(responder));
          return;
        case "document/authorize":
          handle_DocumentAuthorize(requestId, //
          request.getStringNormalize("space", true, 461828), //
          request.getString("key", true, 466947), //
          request.getString("username", true, 458737), //
          request.getString("password", true, 465917), //
          new InitiationResponder(responder));
          return;
        case "document/authorize-domain":
          handle_DocumentAuthorizeDomain(requestId, //
          request.getString("domain", true, 488444), //
          request.getString("username", true, 458737), //
          request.getString("password", true, 465917), //
          new InitiationResponder(responder));
          return;
        case "document/authorize-with-reset":
          handle_DocumentAuthorizeWithReset(requestId, //
          request.getStringNormalize("space", true, 461828), //
          request.getString("key", true, 466947), //
          request.getString("username", true, 458737), //
          request.getString("password", true, 465917), //
          request.getString("new_password", true, 466931), //
          new InitiationResponder(responder));
          return;
        case "document/authorize-domain-with-reset":
          handle_DocumentAuthorizeDomainWithReset(requestId, //
          request.getString("domain", true, 488444), //
          request.getString("username", true, 458737), //
          request.getString("password", true, 465917), //
          request.getString("new_password", true, 466931), //
          new InitiationResponder(responder));
          return;
        case "connection/create":
          handle_ConnectionCreate(requestId, //
          request.getString("identity", true, 458759), //
          request.getStringNormalize("space", true, 461828), //
          request.getString("key", true, 466947), //
          request.getObject("viewer-state", false, 0), //
          new DataResponder(responder));
          return;
        case "connection/create-via-domain":
          handle_ConnectionCreateViaDomain(requestId, //
          request.getString("identity", true, 458759), //
          request.getString("domain", true, 488444), //
          request.getObject("viewer-state", false, 0), //
          new DataResponder(responder));
          return;
        case "connection/send":
          handle_ConnectionSend(requestId, //
          request.getLong("connection", true, 405505), //
          request.getString("channel", true, 454659), //
          request.getJsonNode("message", true, 425987), //
          new SeqResponder(responder));
          return;
        case "connection/password":
          handle_ConnectionPassword(requestId, //
          request.getLong("connection", true, 405505), //
          request.getString("username", true, 458737), //
          request.getString("password", true, 465917), //
          request.getString("new_password", true, 466931), //
          new SimpleResponder(responder));
          return;
        case "connection/send-once":
          handle_ConnectionSendOnce(requestId, //
          request.getLong("connection", true, 405505), //
          request.getString("channel", true, 454659), //
          request.getString("dedupe", false, 0), //
          request.getJsonNode("message", true, 425987), //
          new SeqResponder(responder));
          return;
        case "connection/can-attach":
          handle_ConnectionCanAttach(requestId, //
          request.getLong("connection", true, 405505), //
          new YesResponder(responder));
          return;
        case "connection/attach":
          handle_ConnectionAttach(requestId, //
          request.getLong("connection", true, 405505), //
          request.getString("asset-id", true, 476156), //
          request.getString("filename", true, 470028), //
          request.getString("content-type", true, 455691), //
          request.getLong("size", true, 477179), //
          request.getString("digest-md5", true, 445437), //
          request.getString("digest-sha384", true, 406525), //
          new SeqResponder(responder));
          return;
        case "connection/update":
          handle_ConnectionUpdate(requestId, //
          request.getLong("connection", true, 405505), //
          request.getObject("viewer-state", false, 0), //
          new SimpleResponder(responder));
          return;
        case "connection/end":
          handle_ConnectionEnd(requestId, //
          request.getLong("connection", true, 405505), //
          new SimpleResponder(responder));
          return;
        case "documents/hash-password":
          handle_DocumentsHashPassword(requestId, //
          request.getString("password", true, 465917), //
          new HashedPasswordResponder(responder));
          return;
        case "configure/make-or-get-asset-key":
          handle_ConfigureMakeOrGetAssetKey(requestId, //
          new AssetKeyResponder(responder));
          return;
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}
