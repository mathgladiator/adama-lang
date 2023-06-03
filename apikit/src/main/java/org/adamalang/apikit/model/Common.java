/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
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
    String[] parts = name.split("[-/]");
    StringBuilder result = new StringBuilder();
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
