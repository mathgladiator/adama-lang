package org.adamalang.rxhtml.atl.tree;

/** Negate a node (i.e. not for boolean) */
public class Negate implements Node {
  private final Node value;

  public Negate(Node value) {
    this.value = value;
  }

  @Override
  public String debug() {
    return "!(" + value.debug() + ")";
  }
}
