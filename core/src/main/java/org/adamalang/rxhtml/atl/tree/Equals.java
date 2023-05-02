/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
