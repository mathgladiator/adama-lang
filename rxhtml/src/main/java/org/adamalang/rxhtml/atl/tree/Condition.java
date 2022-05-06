package org.adamalang.rxhtml.atl.tree;

/** an if/then/else node */
public class Condition implements Node {
  public final Node guard;
  public Node branchTrue;
  public Node branchFalse;

  public Condition(Node guard, Node branchTrue, Node branchFalse) {
    this.guard = guard;
    this.branchTrue = branchTrue;
    this.branchFalse = branchFalse;
  }

  @Override
  public String debug() {
    return "(" + guard.debug() + ") ? (" + branchTrue.debug() + ") : (" + branchFalse.debug() + ")";
  }
}
