package org.adamalang.rxhtml.atl.tree;

import java.util.Set;

/** common interface for the tree nodes */
public interface Tree {

  /** return a set of variables within the node */
  public Set<String> variables();

  /** turn the node into an easy to debug string */
  public String debug();

  /** javascript expression to build the string */
  public String js(String env);
}
