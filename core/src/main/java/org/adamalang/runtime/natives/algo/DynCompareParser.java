/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives.algo;

import java.util.ArrayList;
import java.util.regex.Pattern;

/** parse a string into a array that has ascending and descending encoded for a dynamic compare */
public class DynCompareParser {
  public static CompareField[] parse(String dyn) {
    String[] parts = dyn.split(Pattern.quote(","));
    ArrayList<CompareField> result = new ArrayList<>();
    for (String part : parts) {
      part = part.trim();
      if (part.length() == 0) continue;
      char start = part.charAt(0);
      if (start == '+' || start == '-') {
        result.add(new CompareField(part.substring(1).trim(), start == '-'));
      } else {
        result.add(new CompareField(part, false));
      }
    }
    return result.toArray(new CompareField[result.size()]);
  }
}
