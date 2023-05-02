/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.model;

import java.util.Locale;
import java.util.regex.Pattern;

public class Common {
  public static String camelize(String name) {
    return camelize(name, false);
  }

  public static String camelize(String name, boolean lowerFirst) {
    if (name == null || name.length() == 0) {
      return name;
    }
    String[] parts = name.replaceAll(Pattern.quote("/"), "-").split(Pattern.quote("-"));
    StringBuilder result = new StringBuilder();
    boolean lower = lowerFirst;
    for (String part : parts) {
      if (lowerFirst) {
        result.append(part.toLowerCase(Locale.ROOT));
      } else {
        result.append(part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1).toLowerCase(Locale.ROOT));
      }
      lowerFirst = false;
    }
    return result.toString();
  }
}
