/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;

/** wrapper around an object node such that the entire API is consistent */
public class Request {
  private final ObjectNode node;

  public Request(ObjectNode node) {
    this.node = node;
  }

  public int id() throws ErrorCodeException {
    return (int) lng(node, "id", ErrorCodes.USERLAND_REQUEST_NO_ID_PROPERTY);
  }

  public int stream() throws ErrorCodeException {
    return (int) lng(node, "stream", ErrorCodes.USERLAND_REQUEST_NO_STREAM_PROPERTY);
  }

  public String method() throws ErrorCodeException {
    return str(node, "method", true, ErrorCodes.USERLAND_REQUEST_NO_METHOD_PROPERTY);
  }

  public String space() throws ErrorCodeException {
    return str(node, "space", true, ErrorCodes.USERLAND_REQUEST_NO_GAMESPACE_PROPERTY);
  }

  public String marker() throws ErrorCodeException {
    return str(node, "marker", true, ErrorCodes.USERLAND_REQUEST_NO_MARKER_PROPERTY);
  }

  public String channel() throws ErrorCodeException {
    return str(node, "channel", true, ErrorCodes.USERLAND_REQUEST_NO_CHANNEL_PROPERTY);
  }

  public String entropy() throws ErrorCodeException {
    return str(node, "entropy", false, 0);
  }

  public String key() throws ErrorCodeException {
    return str(node, "key", true, ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
  }

  public String json_message() throws ErrorCodeException {
    return node(node, "message", ErrorCodes.USERLAND_REQUEST_NO_MESSAGE_PROPERTY).toString();
  }

  public String json_arg() throws ErrorCodeException {
    return node(node, "arg", ErrorCodes.USERLAND_REQUEST_NO_CONSTRUCTOR_ARG).toString();
  }

  public NtClient impersonate() throws ErrorCodeException {
    final var impersonateNode = node.get("impersonate");
    if (impersonateNode != null && impersonateNode.isObject()) {
      ObjectNode impersonateObject = (ObjectNode) impersonateNode;
      String agent = str(impersonateObject, "agent", true, ErrorCodes.USERLAND_REQUEST_IMPERSONATE_NO_AGENT);
      String authority = str(impersonateObject, "authority", true, ErrorCodes.USERLAND_REQUEST_IMPERSONATE_NO_AUTHORITY);
      return new NtClient(agent, authority);
    }
    return null;
  };

  private static String str(final ObjectNode request, final String field, final boolean mustExist, final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isTextual() || fieldNode.isNumber())) {
      if (mustExist) { throw new ErrorCodeException(errorIfDoesnt); }
      return null;
    }
    if (fieldNode.isNumber()) {
      return fieldNode.numberValue().toString();
    }
    return fieldNode.textValue();
  }


  private static long lng(final ObjectNode request, final String field, final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isNumber() && fieldNode.isIntegralNumber() || fieldNode.isTextual())) {
      throw new ErrorCodeException(errorIfDoesnt);
    }
    if (fieldNode.isTextual()) {
      try {
        return Long.parseLong(fieldNode.textValue());
      } catch (NumberFormatException nfe) {
        throw new ErrorCodeException(errorIfDoesnt);
      }
    }
    return fieldNode.longValue();
  }

  private static JsonNode node(final ObjectNode request, final String field, final int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = request.get(field);
    if (fieldNode == null || fieldNode.isNull() || !fieldNode.isObject()) {
      throw new ErrorCodeException(errorIfDoesnt);
    }
    return fieldNode;
  }
}
