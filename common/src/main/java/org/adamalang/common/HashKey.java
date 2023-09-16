/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.common;

import java.util.HashMap;

/** Generate keys within a map which are unique */
public class HashKey {

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
}
