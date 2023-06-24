/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

/** when we want to encode multiple strings into one string easily */
public class SlashStringArrayEncoder {
  /** encode by packing with slashes as an escaped ('-') delimiter */
  public static String encode(String... fragments) {
    StringBuilder fixed = new StringBuilder();
    boolean appendSlash = false;
    for (String fragment : fragments) {
      if (appendSlash) {
        fixed.append("/");
      } else {
        appendSlash = true;
      }
      PrimitiveIterator.OfInt it = fragment.codePoints().iterator();
      while (it.hasNext()) {
        int cp = it.next();
        switch (cp) {
          case '/':
            fixed.append("-/");
            break;
          case '-':
            fixed.append("--");
            break;
          default:
            fixed.append(Character.toChars(cp));
            break;
        }
      }
    }
    return fixed.toString();
  }

  /** unpack a joined string into an array */
  public static String[] decode(String joined) {
    ArrayList<String> fragments = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    PrimitiveIterator.OfInt it = joined.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.next();
      switch (cp) {
        case '/':
          fragments.add(current.toString());
          current.setLength(0);
          break;
        case '-':
          current.append(Character.toChars(it.next()));
          break;
        default:
          current.append(Character.toChars(cp));
          break;
      }
    }
    fragments.add(current.toString());
    return fragments.toArray(new String[fragments.size()]);
  }
}
