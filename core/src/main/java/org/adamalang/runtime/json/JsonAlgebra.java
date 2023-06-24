/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.AutoMorphicAccumulator;

import java.util.HashMap;
import java.util.Map;

/** merge and roll-forward operations for JSON */
public class JsonAlgebra {
  /** RFC7396 for merging a patch into a target */
  @SuppressWarnings("unchecked")
  public static Object merge(final Object targetObject, final Object patchObject, boolean keepNulls) {
    if (patchObject instanceof HashMap) {
      HashMap<String, Object> patchMap = (HashMap<String, Object>) patchObject;
      if (targetObject instanceof HashMap) {
        HashMap<String, Object> targetMap = (HashMap<String, Object>) targetObject;
        for (Map.Entry<String, Object> patchEntry : patchMap.entrySet()) {
          String key = patchEntry.getKey();
          if (patchEntry.getValue() == null) {
            targetMap.remove(key);
            if (keepNulls) {
              targetMap.put(key, null);
            }
          } else {
            Object result = merge(targetMap.get(key), patchEntry.getValue(), keepNulls);
            if (result != null) {
              targetMap.put(key, result);
            }
          }
        }
        return targetMap;
      } else {
        return merge(new HashMap<String, Object>(), patchObject, keepNulls);
      }
    }
    return patchObject;
  }

  /** Product an object level delta and write it out; note: this doesn't compare items, and only does object fields present/not */
  @SuppressWarnings("unchecked")
  public static void writeObjectFieldDelta(final Object from, final Object to, JsonStreamWriter writer) {
    if (from instanceof HashMap && to instanceof HashMap) {
      HashMap<String, Object> mapFrom = (HashMap<String, Object>) from;
      HashMap<String, Object> mapTo = (HashMap<String, Object>) to;
      writer.beginObject();
      for (Map.Entry<String, Object> entryTo : mapTo.entrySet()) {
        writer.writeObjectFieldIntro(entryTo.getKey());
        Object objFrom = mapFrom.get(entryTo.getKey());
        if (objFrom != null) {
          writeObjectFieldDelta(objFrom, entryTo.getValue(), writer);
        } else {
          writer.writeTree(entryTo.getValue());
        }
      }
      for (Map.Entry<String, Object> entryFrom : mapFrom.entrySet()) {
        if (!mapTo.containsKey(entryFrom.getKey())) {
          writer.writeObjectFieldIntro(entryFrom.getKey());
          writer.writeNull();
        }
      }
      writer.endObject();
    } else {
      writer.writeTree(to);
    }
  }

  /** an accumulator/fold version of merge */
  public static AutoMorphicAccumulator<String> mergeAccumulator() {
    return mergeAccumulator(true);
  }

  public static AutoMorphicAccumulator<String> mergeAccumulator(boolean keepNulls) {
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
          state = merge(state, reader.readJavaTree(), keepNulls);
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
}
