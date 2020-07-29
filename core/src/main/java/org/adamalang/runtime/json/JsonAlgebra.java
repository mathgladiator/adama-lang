/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import java.util.HashMap;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** This is a giant mess, but this is where the current logic sits for patching
 * and diffing json objects. The diffing is currently too new. in favor of
 * code-gen client views */
public class JsonAlgebra {
  public static class LazyJsonWriter {
    private final String fieldName;
    private JsonNode node;
    private final LazyJsonWriter parent;

    private LazyJsonWriter() {
      parent = null;
      node = null;
      fieldName = null;
    }

    private LazyJsonWriter(final LazyJsonWriter parent, final String fieldName) {
      this.parent = parent;
      node = null;
      this.fieldName = fieldName;
    }

    public LazyJsonWriter createChild(final String newFieldName) {
      return new LazyJsonWriter(this, newFieldName);
    }

    public JsonNode getNode() {
      return node;
    }

    public JsonNode manifest() {
      if (node != null) { return node; }
      if (parent == null) {
        node = Utility.createObjectNode();
      } else {
        node = ((ObjectNode) parent.manifest()).putObject(fieldName);
      }
      return node;
    }

    public void writeValue(final JsonNode value) {
      if (parent == null) {
        node = value;
        return;
      }
      if (value == null) {
        ((ObjectNode) parent.manifest()).putNull(fieldName);
      } else {
        ((ObjectNode) parent.manifest()).set(fieldName, value);
      }
    }
  }

  public static JsonNode difference(final JsonNode before, final JsonNode after) {
    final var writer = new LazyJsonWriter();
    difference(before, after, writer);
    return writer.getNode();
  }

  private static void difference(final JsonNode before, final JsonNode after, final LazyJsonWriter writer) {
    if (after == null || after.isNull()) {
      if (before != null && !before.isNull()) {
        writer.writeValue(null);
      }
      return;
    }
    switch (after.getNodeType()) {
      case OBJECT:
        if (before == null || before.isNull()) {
          // we have to manifest the entire object
          final var it = after.fields();
          while (it.hasNext()) {
            final var entry = it.next();
            difference(null, entry.getValue(), writer.createChild(entry.getKey()));
          }
        } else if (before.isObject()) {
          var it = after.fields();
          while (it.hasNext()) {
            final var entry = it.next();
            final var beforeValue = before.get(entry.getKey());
            if (beforeValue == null || beforeValue.isNull()) { // addition
              difference(null, entry.getValue(), writer.createChild(entry.getKey()));
            } else { // change
              difference(beforeValue, entry.getValue(), writer.createChild(entry.getKey()));
            }
          }
          it = before.fields();
          while (it.hasNext()) {
            final var entry = it.next();
            final var afterValue = after.get(entry.getKey());
            if (afterValue == null || afterValue.isNull()) { // deletion
              writer.createChild(entry.getKey()).writeValue(null);
            }
          }
        } else {
          writer.writeValue(after);
        }
        return;
      case ARRAY:
        if (before == null || before.isNull() || !before.isArray()) {
          final var newArray = Utility.createArrayNode();
          final var afterArray = (ArrayNode) after;
          for (var idx = 0; idx < afterArray.size(); idx++) {
            final var child = newArray.addObject();
            child.set("@n", difference(Utility.createObjectNode(), afterArray.get(idx)));
          }
          writer.writeValue(newArray);
        } else {
          final var newArray = Utility.createArrayNode();
          final var beforeArray = (ArrayNode) before;
          // Step 1: Index the prior array by id (if possible)
          final var beforeIdsToIndex = new HashMap<Integer, Integer>();
          for (var idx = 0; idx < beforeArray.size(); idx++) {
            final var id = pullId(beforeArray.get(idx));
            if (id != null) {
              beforeIdsToIndex.put(id, idx);
            }
          }
          // Step 2: Reconstruct the array via a list of commands
          final var afterArray = (ArrayNode) after;
          var writeOut = beforeArray.size() != afterArray.size();
          for (var idx = 0; idx < afterArray.size(); idx++) {
            final var afterElement = afterArray.get(idx);
            // If after element is a value, then short-circuit to a new value
            final var afterId = pullId(afterElement);
            final var child = newArray.addObject();
            if (afterId != null) {
              final var oldIdx = beforeIdsToIndex.get(afterId);
              if (oldIdx != null) {
                final var delta = (ObjectNode) difference(before.get(oldIdx), afterElement);
                if (delta == null) {
                  if (oldIdx != idx) {
                    writeOut = true;
                  }
                  child.put("@c", oldIdx);
                } else {
                  writeOut = true;
                  child.set("@m", difference(beforeArray.get(oldIdx), afterElement));
                  child.put("@f", oldIdx);
                }
              } else {
                writeOut = true;
                child.set("@n", difference(Utility.createObjectNode(), afterElement));
              }
            } else {
              writeOut = true;
              child.set("@n", difference(Utility.createObjectNode(), afterElement));
            }
          }
          if (writeOut) {
            writer.writeValue(newArray);
          }
        }
        return;
      case BOOLEAN:
        if (before == null || before.isNull()) {
          writer.writeValue(after);
        } else if (before.isBoolean()) {
          if (before.booleanValue() != after.booleanValue()) {
            writer.writeValue(after);
          }
        } else {
          writer.writeValue(after);
        }
        return;
      case NUMBER:
        if (before == null || before.isNull()) {
          writer.writeValue(after);
        } else if (before.isNumber()) {
          if (before.asDouble() != after.asDouble()) {
            writer.writeValue(after);
          }
        } else {
          writer.writeValue(after);
        }
        break;
      case STRING:
      default:
        if (before == null || before.isNull()) {
          writer.writeValue(after);
        } else if (before.isTextual()) {
          if (!before.asText().equals(after.asText())) {
            writer.writeValue(after);
          }
        } else {
          writer.writeValue(after);
        }
    }
  }

  public static JsonNode patch(final JsonNode target, final JsonNode patch) {
    if (patch != null && patch.isObject()) {
      if (target != null && target.isObject()) {
        final var it = patch.fields();
        while (it.hasNext()) {
          final var entry = it.next();
          if (entry.getValue().isNull() || entry.getValue() == null) {
            if (target.has(entry.getKey())) {
              ((ObjectNode) target).remove(entry.getKey());
            }
          } else {
            final var result = patch(target.get(entry.getKey()), entry.getValue());
            if (!(result == null || result.isNull())) {
              ((ObjectNode) target).set(entry.getKey(), result);
            }
          }
        }
        return target;
      } else {
        return patch(Utility.createObjectNode(), patch);
      }
    }
    return patch;
  }

  private static Integer pullId(final JsonNode node) {
    if (node != null && node.isObject()) {
      final var idNode = node.get("id");
      if (idNode != null && idNode.isIntegralNumber()) { return idNode.intValue(); }
    }
    return null;
  }
}
