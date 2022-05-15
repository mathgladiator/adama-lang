package org.adamalang.rxhtml.atl.tree;

import java.util.Collections;
import java.util.Set;

public class Empty implements Tree {

  @Override
  public Set<String> variables() {
    return Collections.emptySet();
  }

  @Override
  public String debug() {
    return "EMPTY";
  }

  @Override
  public String js(String env) {
    return "\"\"";
  }
}
