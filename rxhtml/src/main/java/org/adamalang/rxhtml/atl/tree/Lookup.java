package org.adamalang.rxhtml.atl.tree;

import java.util.Collections;
import java.util.Set;

/** lookup a variable */
public class Lookup implements Tree {
  public final String variable;

  public Lookup(String variable) {
    this.variable = variable;
  }

  @Override
  public Set<String> variables() {
    return Collections.singleton(variable);
  }

  @Override
  public String debug() {
    return "LOOKUP[" + variable + "]";
  }

  @Override
  public String js(String env) {
    return env + "." + variable;
  }
}
