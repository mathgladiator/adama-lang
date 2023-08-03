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
import org.adamalang.rxhtml.atl.Parser;

import java.util.Map;
import java.util.TreeMap;

/** a simple way of doing string equality */
public class Equals implements Tree {
  public final Tree tree;
  public final Tree value;

  public Equals(Tree tree, String value) {
    this.tree = tree;
    this.value = Parser.parse(value);
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    union.putAll(tree.variables());
    union.putAll(value.variables());
    return union;
  }

  @Override
  public String debug() {
    return "EQUALS[" + tree.debug() + ",'" + value + "']";
  }

  @Override
  public String js(Context context, String env) {
    return "(" + tree.js(context, env) + "==" + value.js(context, env) + ")";
  }
}
