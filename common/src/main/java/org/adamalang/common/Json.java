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
package org.adamalang.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
    if (node instanceof ObjectNode) { return (ObjectNode) node; }
    throw new Exception("given json is not an ObjectNode at root");
  }

  public static String readString(ObjectNode tree, String field) {
    JsonNode node = tree.get(field);
    if (node == null || node.isNull()) {
      return null;
    }
    return node.textValue();
  }
}
