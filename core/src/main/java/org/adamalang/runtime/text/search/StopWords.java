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
