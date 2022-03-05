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
