/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.AutoMorphicAccumulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** merge and roll-forward operations for JSON */
public class JsonAlgebra {
  /** RFC7396 for merging a patch into a target */
  @SuppressWarnings("unchecked")
  public static Object merge(final Object targetObject, final Object patchObject) {
    if (patchObject instanceof HashMap) {
      HashMap<String, Object> patchMap = (HashMap<String, Object>) patchObject;
      if (targetObject instanceof HashMap) {
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

  /** an accumulator/fold version of merge */
  public static AutoMorphicAccumulator<String> mergeAccumulator() {
    return new AutoMorphicAccumulator<>() {
      private Object state = null;

      @Override
      public boolean empty() {
        return state == null;
      }

      @Override
      public void next(String data) {
        JsonStreamReader reader = new JsonStreamReader(data);
        if (state == null) {
          state = reader.readJavaTree();
        } else {
          state = merge(state, reader.readJavaTree());
        }
      }

      @Override
      public String finish() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(state);
        return writer.toString();
      }
    };
  }

  /** Given an UNDO at a fixed point in time, and a REDO in the future; manipulate the UNDO such that the
   * REDO will cancel out a change from UNDO; return true if the undo becomes empty */
  @SuppressWarnings("unchecked")
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
      }
    }
    return undo.isEmpty();
  }

  /** an accumulator/fold version of rollUndoForward */
  public static AutoMorphicAccumulator<String> rollUndoForwardAccumulator(String undo) {
    return new AutoMorphicAccumulator<>() {
      private HashMap<String, Object> state = (HashMap<String, Object>) new JsonStreamReader(undo).readJavaTree();

      @Override
      public boolean empty() {
        return false;
      }

      @Override
      public void next(String data) {
        rollUndoForward(state, (HashMap<String, Object>) new JsonStreamReader(data).readJavaTree());
      }

      @Override
      public String finish() {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(state);
        return writer.toString();
      }
    };
  }
}
