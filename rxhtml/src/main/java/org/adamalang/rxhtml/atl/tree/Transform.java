package org.adamalang.rxhtml.atl.tree;

/** Transform a node */
public class Transform implements Node {

  public final Node base;
  public final String operation;

  public Transform(Node base, String operation) {
    this.base = base;
    this.operation = operation;
  }

  @Override
  public String debug() {
    return "TRANSFORM(" + base.debug() + "," + operation + ")";
  }
}
