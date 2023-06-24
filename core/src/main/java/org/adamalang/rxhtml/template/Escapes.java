/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import java.util.regex.Pattern;

public class Escapes {
  public static String escapeNewLine(String x) {
    return String.join("\\n", x.split(Pattern.quote("\n"), -1));
  }
  public static String escapeSlash(String x) {
    return String.join("\\\\", x.split(Pattern.quote("\\"), -1));
  }
  public static String escape39(String x) {
    return String.join("\\'", escapeNewLine(escapeSlash(x)).split(Pattern.quote("'"), -1));
  }
  public static String escape34(String x) {
    return String.join("\\\"", escapeNewLine(escapeSlash(x)).split(Pattern.quote("\""), -1));
  }

  public static String constantOf(String value) {
    String processedValue = "'" + Escapes.escape39(value) + "'";
    try {
      Double.parseDouble(value);
      processedValue = value;
    } catch (NumberFormatException nfe) {
    }
    if (value.equals("true") || value.equals("false")) {
      processedValue = value;
    }
    return processedValue;
  }

}
