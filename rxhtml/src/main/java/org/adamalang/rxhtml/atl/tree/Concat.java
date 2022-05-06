package org.adamalang.rxhtml.atl.tree;

import java.util.Set;
import java.util.TreeSet;

public class Concat implements Node {
  public final Node[] children;

  public Concat(Node... children) {
    this.children = children;
  }

  @Override
  public Set<String> variables() {
    TreeSet<String> union = new TreeSet<>();
    for (Node child : children) {
      union.addAll(child.variables());
    }
    return union;
  }

  @Override
  public String debug() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    if (children.length > 0) {
      sb.append(children[0].debug());
    }
    for (int k = 1; k < children.length; k++) {
      sb.append(",");
      sb.append(children[k].debug());
    }
    sb.append("]");
    return sb.toString();
  }
}
