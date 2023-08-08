/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.text.search;

import java.util.TreeSet;

/** a list of words to not consider in a search */
public class StopWords {
  public static final TreeSet<String> LIST = build();

  private static TreeSet<String> build() {
    TreeSet<String> list = new TreeSet<>();
    list.add("the");
    list.add("of");
    list.add("a");
    return list;
  }
}
