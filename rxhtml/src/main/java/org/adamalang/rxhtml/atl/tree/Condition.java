package org.adamalang.rxhtml.atl.tree;

import java.util.Set;
import java.util.TreeSet;

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
  public Set<String> variables() {
    TreeSet<String> union = new TreeSet<>();
    union.addAll(guard.variables());
    union.addAll(branchTrue.variables());
    union.addAll(branchFalse.variables());
    return union;
  }

  @Override
  public String debug() {
    return "(" + guard.debug() + ") ? (" + branchTrue.debug() + ") : (" + branchFalse.debug() + ")";
  }

  @Override
  public String js(String env) {
    return "(" + guard.js(env) + ") ? (" + branchTrue.js(env) + ") : (" + branchFalse.js(env) + ")";
  }
}
