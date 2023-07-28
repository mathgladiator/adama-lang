/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.rxhtml.atl.Context;

import java.util.Map;
import java.util.TreeMap;

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
  public String js(Context context, String env) {
    StringBuilder sb = new StringBuilder();
    sb.append(children[0].js(context, env));
    for (int k = 1; k < children.length; k++) {
      boolean skip = false;
      if (children[k] instanceof Text) {
        skip = ((Text) children[k]).skip(context);
      }
      if (!skip) {
        sb.append(" + ");
        sb.append(children[k].js(context, env));
      }
    }
    return sb.toString();
  }
}
