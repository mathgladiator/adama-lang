/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import java.util.Map;

/** a simple way of doing string equality */
public class Equals implements Tree {
  public final Tree tree;
  public final String value;

  public Equals(Tree tree, String value) {
    this.tree = tree;
    this.value = value;
  }

  @Override
  public Map<String, String> variables() {
    return tree.variables();
  }

  @Override
  public String debug() {
    return "EQUALS[" + tree.debug() + ",'" + value + "']";
  }

  @Override
  public String js(String env) {
    return "(" + tree.js(env) + "=='" + value + "')";
  }
}
