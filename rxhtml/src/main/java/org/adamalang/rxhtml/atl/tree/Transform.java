package org.adamalang.rxhtml.atl.tree;

import java.util.Set;

/** Transform a node */
public class Transform implements Tree {

  public final Tree base;
  public final String operation;

  public Transform(Tree base, String operation) {
    this.base = base;
    this.operation = operation;
  }

  @Override
  public Set<String> variables() {
    return base.variables();
  }

  @Override
  public String debug() {
    return "TRANSFORM(" + base.debug() + "," + operation + ")";
  }

  @Override
  public String js(String env) {
    // TODO: big table
    return operation + "(" + base.js(env) + ")";
  }
}
