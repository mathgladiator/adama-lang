package org.adamalang.rxhtml.tree;

public interface Item {

  boolean varies();
  void dump(int depth);

  String html();
}
