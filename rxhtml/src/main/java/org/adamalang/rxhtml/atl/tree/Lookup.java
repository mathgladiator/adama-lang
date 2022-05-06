package org.adamalang.rxhtml.atl.tree;

/** lookup a variable */
public class Lookup implements Node {
  public final String variable;

  public Lookup(String variable) {
    this.variable = variable;
  }

  @Override
  public String debug() {
    return "LOOKUP[" + variable + "]";
  }
}
