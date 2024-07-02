/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DevBoxRouter {
  private static final Logger ACCESS_LOG = LoggerFactory.getLogger("access");
  private static final JsonLogger DEV_ACCESS_LOG = (item) -> ACCESS_LOG.debug(item.toString());

  public abstract void handle_Stats(long requestId, StatsResponder responder);

  public abstract void handle_IdentityHash(long requestId, String identity, IdentityHashResponder responder);

  public abstract void handle_IdentityStash(long requestId, String identity, String name, SimpleResponder responder);

  public abstract void handle_SpaceReflect(long requestId, String identity, String space, String key, ReflectionResponder responder);

  public abstract void handle_PushRegister(long requestId, String identity, String domain, ObjectNode subscription, ObjectNode deviceInfo, SimpleResponder responder);

  public abstract void handle_DomainReflect(long requestId, String identity, String domain, ReflectionResponder responder);

  public abstract void handle_DomainGetVapidPublicKey(long requestId, String identity, String domain, DomainVapidResponder responder);

  public abstract void handle_DocumentAuthorization(long requestId, String space, String key, JsonNode message, InitiationResponder responder);

  public abstract void handle_DocumentAuthorizationDomain(long requestId, String domain, JsonNode message, InitiationResponder responder);

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

  public abstract void handle_FeatureSummarizeUrl(long requestId, String identity, String url, SummaryResponder responder);

  public abstract void handle_AttachmentStart(long requestId, String identity, String space, String key, String filename, String contentType, ProgressResponder responder);

  public abstract void handle_AttachmentStartByDomain(long requestId, String identity, String domain, String filename, String contentType, ProgressResponder responder);

  public abstract void handle_AttachmentAppend(long requestId, Long upload, String chunkMd5, String base64Bytes, SimpleResponder responder);

  public abstract void handle_AttachmentFinish(long requestId, Long upload, AssetIdResponder responder);

  public void route(JsonRequest request, JsonResponder responder) {
    try {
      long requestId = request.id();
      String method = request.method();
      ObjectNode _accessLogItem = Json.newJsonObject();
      _accessLogItem.put("method", method);
      _accessLogItem.put("requestId", requestId);
      _accessLogItem.put("@timestamp", LogTimestamp.now());
      request.dumpIntoLog(_accessLogItem);
      switch (method) {
        case "stats":
          handle_Stats(requestId, //
            new StatsResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "identity/hash":
          handle_IdentityHash(requestId, //
            request.getString("identity", true, 458759), //
            new IdentityHashResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "identity/stash":
          _accessLogItem.put("name", request.getString("name", true, 453647));
          handle_IdentityStash(requestId, //
            request.getString("identity", true, 458759), //
            request.getString("name", true, 453647), //
            new SimpleResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "space/reflect":
          _accessLogItem.put("space", request.getStringNormalize("space", true, 461828));
          _accessLogItem.put("key", request.getString("key", true, 466947));
          handle_SpaceReflect(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("space", true, 461828), //
            request.getString("key", true, 466947), //
            new ReflectionResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "push/register":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_PushRegister(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("domain", true, 488444), //
            request.getObject("subscription", true, 407308), //
            request.getObject("device-info", true, 446218), //
            new SimpleResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "domain/reflect":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_DomainReflect(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("domain", true, 488444), //
            new ReflectionResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "domain/get-vapid-public-key":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_DomainGetVapidPublicKey(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("domain", true, 488444), //
            new DomainVapidResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "document/authorization":
          _accessLogItem.put("space", request.getStringNormalize("space", true, 461828));
          _accessLogItem.put("key", request.getString("key", true, 466947));
          handle_DocumentAuthorization(requestId, //
            request.getStringNormalize("space", true, 461828), //
            request.getString("key", true, 466947), //
            request.getJsonNode("message", true, 425987), //
            new InitiationResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "document/authorization-domain":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_DocumentAuthorizationDomain(requestId, //
            request.getStringNormalize("domain", true, 488444), //
            request.getJsonNode("message", true, 425987), //
            new InitiationResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "document/authorize":
          _accessLogItem.put("space", request.getStringNormalize("space", true, 461828));
          _accessLogItem.put("key", request.getString("key", true, 466947));
          handle_DocumentAuthorize(requestId, //
            request.getStringNormalize("space", true, 461828), //
            request.getString("key", true, 466947), //
            request.getString("username", true, 458737), //
            request.getString("password", true, 465917), //
            new InitiationResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "document/authorize-domain":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_DocumentAuthorizeDomain(requestId, //
            request.getStringNormalize("domain", true, 488444), //
            request.getString("username", true, 458737), //
            request.getString("password", true, 465917), //
            new InitiationResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "document/authorize-with-reset":
          _accessLogItem.put("space", request.getStringNormalize("space", true, 461828));
          _accessLogItem.put("key", request.getString("key", true, 466947));
          handle_DocumentAuthorizeWithReset(requestId, //
            request.getStringNormalize("space", true, 461828), //
            request.getString("key", true, 466947), //
            request.getString("username", true, 458737), //
            request.getString("password", true, 465917), //
            request.getString("new_password", true, 466931), //
            new InitiationResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "document/authorize-domain-with-reset":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_DocumentAuthorizeDomainWithReset(requestId, //
            request.getStringNormalize("domain", true, 488444), //
            request.getString("username", true, 458737), //
            request.getString("password", true, 465917), //
            request.getString("new_password", true, 466931), //
            new InitiationResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/create":
          _accessLogItem.put("space", request.getStringNormalize("space", true, 461828));
          _accessLogItem.put("key", request.getString("key", true, 466947));
          handle_ConnectionCreate(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("space", true, 461828), //
            request.getString("key", true, 466947), //
            request.getObject("viewer-state", false, 0), //
            new DataResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/create-via-domain":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          handle_ConnectionCreateViaDomain(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("domain", true, 488444), //
            request.getObject("viewer-state", false, 0), //
            new DataResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/send":
          _accessLogItem.put("channel", request.getString("channel", true, 454659));
          handle_ConnectionSend(requestId, //
            request.getLong("connection", true, 405505), //
            request.getString("channel", true, 454659), //
            request.getJsonNode("message", true, 425987), //
            new SeqResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/password":
          handle_ConnectionPassword(requestId, //
            request.getLong("connection", true, 405505), //
            request.getString("username", true, 458737), //
            request.getString("password", true, 465917), //
            request.getString("new_password", true, 466931), //
            new SimpleResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/send-once":
          _accessLogItem.put("channel", request.getString("channel", true, 454659));
          _accessLogItem.put("dedupe", request.getString("dedupe", false, 0));
          handle_ConnectionSendOnce(requestId, //
            request.getLong("connection", true, 405505), //
            request.getString("channel", true, 454659), //
            request.getString("dedupe", false, 0), //
            request.getJsonNode("message", true, 425987), //
            new SeqResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/can-attach":
          handle_ConnectionCanAttach(requestId, //
            request.getLong("connection", true, 405505), //
            new YesResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/attach":
          _accessLogItem.put("asset-id", request.getString("asset-id", true, 476156));
          _accessLogItem.put("filename", request.getString("filename", true, 470028));
          _accessLogItem.put("content-type", request.getString("content-type", true, 455691));
          _accessLogItem.put("size", request.getLong("size", true, 477179));
          _accessLogItem.put("digest-md5", request.getString("digest-md5", true, 445437));
          _accessLogItem.put("digest-sha384", request.getString("digest-sha384", true, 406525));
          handle_ConnectionAttach(requestId, //
            request.getLong("connection", true, 405505), //
            request.getString("asset-id", true, 476156), //
            request.getString("filename", true, 470028), //
            request.getString("content-type", true, 455691), //
            request.getLong("size", true, 477179), //
            request.getString("digest-md5", true, 445437), //
            request.getString("digest-sha384", true, 406525), //
            new SeqResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/update":
          handle_ConnectionUpdate(requestId, //
            request.getLong("connection", true, 405505), //
            request.getObject("viewer-state", false, 0), //
            new SimpleResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "connection/end":
          handle_ConnectionEnd(requestId, //
            request.getLong("connection", true, 405505), //
            new SimpleResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "documents/hash-password":
          handle_DocumentsHashPassword(requestId, //
            request.getString("password", true, 465917), //
            new HashedPasswordResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "feature/summarize-url":
          handle_FeatureSummarizeUrl(requestId, //
            request.getString("identity", true, 458759), //
            request.getString("url", true, 423142), //
            new SummaryResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "attachment/start":
          _accessLogItem.put("space", request.getStringNormalize("space", true, 461828));
          _accessLogItem.put("key", request.getString("key", true, 466947));
          _accessLogItem.put("filename", request.getString("filename", true, 470028));
          _accessLogItem.put("content-type", request.getString("content-type", true, 455691));
          handle_AttachmentStart(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("space", true, 461828), //
            request.getString("key", true, 466947), //
            request.getString("filename", true, 470028), //
            request.getString("content-type", true, 455691), //
            new ProgressResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "attachment/start-by-domain":
          _accessLogItem.put("domain", request.getStringNormalize("domain", true, 488444));
          _accessLogItem.put("filename", request.getString("filename", true, 470028));
          _accessLogItem.put("content-type", request.getString("content-type", true, 455691));
          handle_AttachmentStartByDomain(requestId, //
            request.getString("identity", true, 458759), //
            request.getStringNormalize("domain", true, 488444), //
            request.getString("filename", true, 470028), //
            request.getString("content-type", true, 455691), //
            new ProgressResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "attachment/append":
          handle_AttachmentAppend(requestId, //
            request.getLong("upload", true, 409609), //
            request.getString("chunk-md5", true, 462859), //
            request.getString("base64-bytes", true, 409608), //
            new SimpleResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
        case "attachment/finish":
          handle_AttachmentFinish(requestId, //
            request.getLong("upload", true, 409609), //
            new AssetIdResponder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));
          return;
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}
