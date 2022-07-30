/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
