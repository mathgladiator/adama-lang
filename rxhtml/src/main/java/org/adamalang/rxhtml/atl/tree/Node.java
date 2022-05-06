package org.adamalang.rxhtml.atl.tree;

import java.util.Set;

/** common interface for the tree nodes */
public interface Node {

  /** return a set of variables within the node */
  public Set<String> variables();

  /** turn the node into an easy to debug string */
  public String debug();
}
