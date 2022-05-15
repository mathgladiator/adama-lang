package org.adamalang.rxhtml.atl.tree;

import java.util.Set;
import java.util.TreeSet;

public class Concat implements Tree {
  public final Tree[] children;

  public Concat(Tree... children) {
    this.children = children;
  }

  @Override
  public Set<String> variables() {
    TreeSet<String> union = new TreeSet<>();
    for (Tree child : children) {
      union.addAll(child.variables());
    }
    return union;
  }

  @Override
  public String debug() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(children[0].debug());
    for (int k = 1; k < children.length; k++) {
      sb.append(",");
      sb.append(children[k].debug());
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public String js(String env) {
    StringBuilder sb = new StringBuilder();
    sb.append(children[0].js(env));
    for (int k = 1; k < children.length; k++) {
      sb.append(" + ");
      sb.append(children[k].js(env));
    }
    return sb.toString();
  }
}
