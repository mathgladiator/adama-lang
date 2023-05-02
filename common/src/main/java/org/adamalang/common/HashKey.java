/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import java.util.HashMap;

/** Generate keys within a map which are unique */
public class HashKey {

  /** produce a unique key; single round */
  private static String round(String item, String suffix, HashMap<String, String> map) {
    String candidate = Integer.toString(Math.abs(item.hashCode()), 36) + suffix;
    for (int k = 1; k < candidate.length(); k++) {
      String test = candidate.substring(0, k);
      if (!map.containsKey(test)) {
        return test;
      }
    }
    return null;
  }

  /** produce a unique key */
  public static String keyOf(String item, HashMap<String, String> map) {
    String suffix = ""; // we start optimistically, assuming no suffix to reduce compute waste
    String result = round(item, suffix, map);
    while (result == null) {
      suffix = Long.toString((long) (System.currentTimeMillis() * Math.random()), 16);
      result = round(item, suffix, map);
    }
    return result;
  }
}
