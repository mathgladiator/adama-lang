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
package org.adamalang.web.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;

import java.util.Locale;

/** a thin wrapper for easy access to a JSON request */
public class JsonRequest {
  public final ConnectionContext context;
  private final ObjectNode node;

  public JsonRequest(ObjectNode node, ConnectionContext context) {
    this.node = node;
    this.context = context;
  }

  public int id() throws ErrorCodeException {
    return getInteger("id", true, ErrorCodes.USERLAND_REQUEST_NO_ID_PROPERTY);
  }

  public Integer getInteger(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isNumber() && fieldNode.isIntegralNumber() || fieldNode.isTextual())) {
      if (mustExist) {
        throw new ErrorCodeException(errorIfDoesnt);
      } else {
        return null;
      }
    }
    if (fieldNode.isTextual()) {
      try {
        return Integer.parseInt(fieldNode.textValue());
      } catch (NumberFormatException nfe) {
        throw new ErrorCodeException(errorIfDoesnt);
      }
    }
    return fieldNode.intValue();
  }

  public void dumpIntoLog(ObjectNode logItem) {
    logItem.put("ip", context.remoteIp);
    logItem.put("origin", context.origin);
  }

  public String method() throws ErrorCodeException {
    return getString("method", true, ErrorCodes.USERLAND_REQUEST_NO_METHOD_PROPERTY);
  }

  public String getString(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isTextual() || fieldNode.isNumber())) {
      if (mustExist) {
        throw new ErrorCodeException(errorIfDoesnt);
      }
      return null;
    }
    if (fieldNode.isNumber()) {
      return fieldNode.numberValue().toString();
    }
    return fieldNode.textValue();
  }

  public String getStringNormalize(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    String value = getString(field, mustExist, errorIfDoesnt);
    if (value != null) {
      return value.toLowerCase(Locale.ENGLISH).trim();
    }
    return value;
  }

  public Boolean getBoolean(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull() || !fieldNode.isBoolean()) {
      if (mustExist) {
        throw new ErrorCodeException(errorIfDoesnt);
      }
      return null;
    }
    return fieldNode.booleanValue();
  }

  public Long getLong(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isNumber() && fieldNode.isIntegralNumber() || fieldNode.isTextual())) {
      if (mustExist) {
        throw new ErrorCodeException(errorIfDoesnt);
      } else {
        return null;
      }
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

  public ObjectNode getObject(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull() || !fieldNode.isObject()) {
      if (mustExist) {
        throw new ErrorCodeException(errorIfDoesnt);
      } else {
        return null;
      }
    }
    return (ObjectNode) fieldNode;
  }

  public JsonNode getJsonNode(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
    final var fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isObject() || fieldNode.isArray())) {
      if (mustExist) {
        throw new ErrorCodeException(errorIfDoesnt);
      } else {
        return null;
      }
    }
    return fieldNode;
  }
}
