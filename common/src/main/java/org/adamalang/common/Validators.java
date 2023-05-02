/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
      boolean good = 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z' || '0' <= ch && ch <= '9' || ch == '.' || ch == '-' || ch == '_';
      if (!good) {
        return false;
      }
    }
    return true;
  }
}
