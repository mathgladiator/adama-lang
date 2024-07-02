/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
