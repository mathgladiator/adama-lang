/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

/** Validators used to protect users, enforce system limits, and keep things tidy */
public class Validators {

  /** Validate that the string is a identifier of sorts with few special characters */
  public static boolean simple(String str, int max) {
    if (str.length() > max) {
      return false;
    }
    for (int k = 0; k < str.length(); k++) {
      char ch = str.charAt(k);
      boolean good = 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z' || '0' <= ch && ch <= '9' || ch == '.' || ch == '-' || ch == '_' || ch == '/' || ch == '#' || ch == '=' || ch == '+';
      if (!good) {
        return false;
      }
    }
    return true;
  }
}
