/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
}
