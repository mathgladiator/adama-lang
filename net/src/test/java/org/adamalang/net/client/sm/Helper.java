/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.sm;

import java.util.TreeSet;

public class Helper {
  public static TreeSet<String> setOf(String... strs) {
    TreeSet<String> set = new TreeSet<>();
    for (String str : strs) {
      set.add(str);
    }
    return set;
  }
}
