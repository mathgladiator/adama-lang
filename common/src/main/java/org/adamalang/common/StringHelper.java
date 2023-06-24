/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import java.util.regex.Pattern;

/** help with strings */
public class StringHelper {

  /** split the given text by \n, and then rejoin with each line prefixed by the given tab */
  public static String splitNewlineAndTabify(String text, String tab) {
    StringBuilder sb = new StringBuilder();
    String[] lines = text.split(Pattern.quote("\n"));
    for (String ln : lines) {
      sb.append(tab + ln.stripTrailing() + "\n");
    }
    return sb.toString();
  }
}
