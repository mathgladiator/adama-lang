package org.adamalang.lsp;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelp {
  public static final JsonMapper MAPPER = new JsonMapper();

  public static ArrayNode createArrayNode() {
    return MAPPER.createArrayNode();
  }

  public static ObjectNode createObjectNode() {
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
}
