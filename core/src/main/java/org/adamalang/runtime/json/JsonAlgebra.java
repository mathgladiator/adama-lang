/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
}
