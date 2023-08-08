/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.text.search.Tokenizer;

import java.util.TreeSet;

/** simple search related functions */
public class LibSearch {
  /** operator for searching operator ?= */
  public static boolean test(String needle, String haystack) {
    TreeSet<String> a = Tokenizer.of(needle);
    TreeSet<String> b = Tokenizer.of(haystack);
    for (String x : a) {
      if (b.contains(x)) {
        return true;
      }
    }
    return false;
  }
}
