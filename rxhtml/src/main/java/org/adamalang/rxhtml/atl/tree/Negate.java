package org.adamalang.rxhtml.atl.tree;

import java.util.Set;

/** Negate a node (i.e. not for boolean) */
public class Negate implements Tree {
  private final Tree value;

  public Negate(Tree value) {
    this.value = value;
  }

  @Override
  public Set<String> variables() {
    return value.variables();
  }

  @Override
  public String debug() {
    return "!(" + value.debug() + ")";
  }

  @Override
  public String js(String env) {
    return "!(" + value.js(env) + ")";
  }
}
