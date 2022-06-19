/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.atl.tree;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Concat implements Tree {
  public final Tree[] children;

  public Concat(Tree... children) {
    this.children = children;
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    for (Tree child : children) {
      union.putAll(child.variables());
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
