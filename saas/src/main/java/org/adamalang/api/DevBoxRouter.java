/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

public abstract class DevBoxRouter {

  public abstract void handle_SpaceReflect(long requestId, String identity, String space, String key, ReflectionResponder responder);

  public abstract void handle_ConnectionCreate(long requestId, String identity, String space, String key, ObjectNode viewerState, DataResponder responder);

  public abstract void handle_ConnectionCreateViaDomain(long requestId, String identity, String domain, ObjectNode viewerState, DataResponder responder);

  public abstract void handle_ConnectionSend(long requestId, Long connection, String channel, JsonNode message, SeqResponder responder);

  public abstract void handle_ConnectionPassword(long requestId, Long connection, String username, String password, String new_password, SeqResponder responder);

  public abstract void handle_ConnectionSendOnce(long requestId, Long connection, String channel, String dedupe, JsonNode message, SeqResponder responder);

  public abstract void handle_ConnectionCanAttach(long requestId, Long connection, YesResponder responder);

  public abstract void handle_ConnectionAttach(long requestId, Long connection, String assetId, String filename, String contentType, Long size, String digestMd5, String digestSha384, SeqResponder responder);

  public abstract void handle_ConnectionUpdate(long requestId, Long connection, ObjectNode viewerState, SimpleResponder responder);

  public abstract void handle_ConnectionEnd(long requestId, Long connection, SimpleResponder responder);

  public abstract void handle_DocumentsHashPassword(long requestId, String password, HashedPasswordResponder responder);

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
          new SeqResponder(responder));
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
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}
