/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonDeltaIntegrate {
  /** implements the core integration algorithm that deltas emit */
  public static void integrateDeltaIntoRoot(HashMap<String, Object> root, HashMap<String, Object> delta) {
    for (Map.Entry<String, Object> entry : delta.entrySet()) {
      String secretKey = "#" + entry.getKey();
      Object prior = root.get(entry.getKey());
      Object secretValue = root.get(secretKey);
      Object next = entry.getValue();
      if (next == null) {
        if (prior != null) {
          root.remove(secretKey);
        }
        root.remove(entry.getKey());
      } else {
        if (next instanceof HashMap) {
          HashMap<String, Object> nextMap = (HashMap<String, Object>) next;
          boolean arr = prior instanceof ArrayList || nextMap.containsKey("@o") || nextMap.containsKey("@s");
          if (arr) {
            if (prior == null) {
              prior = new ArrayList<>();
              root.put(entry.getKey(), prior);
            }
            if (secretValue == null) {
              HashMap secretMap = new HashMap<>();
              if (nextMap.containsKey("@o")) {
                secretMap.put("@o", true);
              }
              secretMap.put("__key", entry.getKey());
              secretValue = secretMap;
              root.put(secretKey, secretMap);
            }
            HashMap<String, Object> base = (HashMap<String, Object>) secretValue;
            ArrayList<Object> ordering = null;
            Integer resize = null;
            for (Map.Entry<String, Object> nextEntry : nextMap.entrySet()) {
              Object val = nextEntry.getValue();
              if ("@o".equals(nextEntry.getKey())) {
                ordering = (ArrayList<Object>) val;
              } else if ("@s".equals(nextEntry.getKey())) {
                resize = (Integer) val;
              } else if (val == null) {
                base.remove(nextEntry.getKey());
              } else {
                if (val instanceof HashMap) {
                  HashMap<String, Object> baseItem = (HashMap<String, Object>) base.get(nextEntry.getKey());
                  if (baseItem == null) {
                    baseItem = new HashMap<>();
                    baseItem.put("__key", nextEntry.getKey());
                    base.put(nextEntry.getKey(), baseItem);
                  }
                  integrateDeltaIntoRoot(baseItem, (HashMap<String, Object>) val);
                } else {
                  base.put(nextEntry.getKey(), val);
                }
              }
            }
            if (ordering != null) {
              ArrayList<Object> priorArr = (ArrayList<Object>) prior;
              ArrayList<Object> after = new ArrayList<>();
              for (Object instr : ordering) {
                if (instr instanceof Integer || instr instanceof String) {
                  after.add(base.get("" + instr));
                } else if (instr instanceof ArrayList) {
                  int start = ((ArrayList<Integer>) instr).get(0);
                  int end = ((ArrayList<Integer>) instr).get(1);
                  for (int j = start; j <= end; j++) {
                    after.add(priorArr.get(j));
                  }
                }
              }
              root.put(entry.getKey(), after);
            } else if (resize != null) {
              ArrayList<Object> priorArr = (ArrayList<Object>) prior;
              ArrayList<Object> after = new ArrayList<>();
              for (int k = 0; k < resize; k++) {
                after.add(priorArr.get(k));
              }
              root.put(entry.getKey(), after);
            }
          } else {
            if (prior == null || !(prior instanceof HashMap)) {
              prior = new HashMap<>();
              root.put(entry.getKey(), prior);
            }
            integrateDeltaIntoRoot((HashMap<String, Object>) prior, nextMap);
          }
        } else {
          root.put(entry.getKey(), next);
        }
      }
    }
  }
}
