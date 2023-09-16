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
package org.adamalang.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** helpful toolss for Jackson JSON library */
public class Json {
  public static final JsonMapper MAPPER = new JsonMapper();

  public static ObjectNode newJsonObject() {
    return MAPPER.createObjectNode();
  }

  public static ObjectNode parseJsonObject(final String json) {
    try {
      return parseJsonObjectThrows(json);
    } catch (final Exception jpe) {
      throw new RuntimeException(jpe);
    }
  }

  public static ObjectNode parseJsonObjectThrows(final String json) throws Exception {
    final var node = MAPPER.readTree(json);
    if (node instanceof ObjectNode) {
      return (ObjectNode) node;
    }
    throw new Exception("given json is not an ObjectNode at root");
  }


  public static JsonNode parse(final String json) {
    try {
      return MAPPER.readTree(json);
    } catch (Exception jpe) {
      throw new RuntimeException(jpe);
    }
  }

  public static ArrayNode parseJsonArray(final String json) {
    try {
      return parseJsonArrayThrows(json);
    } catch (final Exception jpe) {
      throw new RuntimeException(jpe);
    }
  }

  public static ArrayNode parseJsonArrayThrows(final String json) throws Exception {
    final var node = MAPPER.readTree(json);
    if (node instanceof ArrayNode) {
      return (ArrayNode) node;
    }
    throw new Exception("given json is not an ArrayNode at root");
  }

  public static String readString(ObjectNode tree, String field) {
    JsonNode node = tree.get(field);
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isTextual()) {
      return node.textValue();
    }
    return node.toString();
  }

  public static String readStringAndRemove(ObjectNode tree, String field) {
    JsonNode node = tree.remove(field);
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isTextual()) {
      return node.textValue();
    }
    return node.toString();
  }

  public static Boolean readBool(ObjectNode tree, String field) {
    JsonNode node = tree.get(field);
    if (node == null || node.isNull() || !node.isBoolean()) {
      return null;
    }
    return node.booleanValue();
  }


  public static boolean readBool(ObjectNode tree, String field, boolean defaultValue) {
    JsonNode node = tree.get(field);
    if (node == null || node.isNull() || !node.isBoolean()) {
      return defaultValue;
    }
    return node.booleanValue();
  }

  public static Integer readInteger(ObjectNode tree, String field) {
    Long lng = readLong(tree, field);
    if (lng != null) {
      return lng.intValue();
    }
    return null;
  }

  public static int readInteger(ObjectNode tree, String field, int defaultValue) {
    Integer ival = readInteger(tree, field);
    if (ival == null) {
      return defaultValue;
    }
    return ival.intValue();
  }

  public static Long readLong(ObjectNode tree, String field) {
    JsonNode node = tree.get(field);
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isIntegralNumber()) {
      return node.longValue();
    }
    if (node.isTextual()) {
      try {
        return Long.parseLong(node.textValue());
      } catch (NumberFormatException nfe) {
        return null;
      }
    }
    return null;
  }

  public static ObjectNode readObject(ObjectNode tree, String field) {
    JsonNode node = tree.get(field);
    if (node == null || node.isNull() || !node.isObject()) {
      return null;
    }
    return (ObjectNode) node;
  }

  public static JsonNode readJsonNode(ObjectNode tree, String field) {
    return tree.get(field);
  }
}
