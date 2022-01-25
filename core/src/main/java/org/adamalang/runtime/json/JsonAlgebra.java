/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
}
