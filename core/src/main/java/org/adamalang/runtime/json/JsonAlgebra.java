/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

/** This is a giant mess, but this is where the current logic sits for patching
 * and diffing json objects. The diffing is currently too new. in favor of
 * code-gen client views */
public class JsonAlgebra {
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

  /** Given an UNDO at a fixed point in time, and a REDO in the future; manipulate the UNDO such that the
   * REDO will cancel out a change from UNDO */
  public static void rollUndoForward(ObjectNode undo, ObjectNode futureRedo) {
    Iterator<Map.Entry<String, JsonNode>> it = undo.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      JsonNode other = futureRedo.get(entry.getKey());
      if (other == null) {
        // preserve
      } else if (entry.getValue().isObject() && other.isObject()) {
        // they are both objects, recurse
        ObjectNode mine = (ObjectNode) entry.getValue();
        rollUndoForward(mine, (ObjectNode) other);
        if (mine.isEmpty()) {
          it.remove();
        }
      } else {
        it.remove();
      }
    }
  }
}
