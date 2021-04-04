/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** merge and roll-forward operations for JSON */
public class JsonAlgebra {
  /** RFC7396 for merging a patch into a target */
  public static Object merge(final Object targetObject, final Object patchObject) {
    if (patchObject != null && patchObject instanceof HashMap) {
      HashMap<String, Object> patchMap = (HashMap<String, Object>) patchObject;
      if (targetObject != null && targetObject instanceof HashMap) {
        HashMap<String, Object> targetMap = (HashMap<String, Object>) targetObject;
        for (Map.Entry<String, Object> patchEntry : patchMap.entrySet()) {
          String key = patchEntry.getKey();
          if (patchEntry.getValue() == null) {
            targetMap.remove(key);
          } else {
            Object result = merge(targetMap.get(key), patchEntry.getValue());
            if (result != null) {
              targetMap.put(key, result);
            }
          }
        }
        return targetMap;
      } else {
        return merge(new HashMap<String, Object>(), patchObject);
      }
    }
    return patchObject;
  }

  /** Given an UNDO at a fixed point in time, and a REDO in the future; manipulate the UNDO such that the
   * REDO will cancel out a change from UNDO; return true if the undo becomes empty */
  public static boolean rollUndoForward(HashMap<String, Object> undo, HashMap<String, Object> futureRedo) {
    Iterator<Map.Entry<String, Object>> it = undo.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Object> entry = it.next();
      if (futureRedo.containsKey(entry.getKey())) {
        Object other = futureRedo.get(entry.getKey());
        boolean remove = true;
        if (entry.getValue() instanceof HashMap && other instanceof HashMap) {
          remove = rollUndoForward((HashMap<String, Object>) entry.getValue(), (HashMap<String, Object>) other);
        }
        if (remove) {
          it.remove();
        }
      } else {
        // preserve
      }
    }
    return undo.isEmpty();
  }

  @Deprecated
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
  @Deprecated
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
