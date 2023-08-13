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
import org.adamalang.translator.reflect.Skip;

import java.util.Locale;
import java.util.TreeSet;

/** simple search related functions */
public class LibSearch {
  /** operator for searching operator =?

   * X =? Y is true IF
   *   * X is empty string
   *   * X is a substring of Y
   *   * X has words that are zpart of the haystack
   * */
  @Skip
  public static boolean test(String needleRaw, String haystackRaw) {
    String needle = needleRaw.trim().toLowerCase(Locale.ENGLISH);
    // condition 1: needle is empty string
    if ("".equals(needle)) {
      return true;
    }

    // condition 2: the entire substring was found within the haystack
    String haystack = haystackRaw.trim().toLowerCase(Locale.ENGLISH);
    if (haystack.contains(needle.trim())) {
      return true;
    }

    // condition 3: we tokenize and check each word is within haystack
    TreeSet<String> a = Tokenizer.of(needle);
    for (String x : a) {
      if (haystack.contains(x)) {
        return true;
      }
    }
    return false;
  }
}
