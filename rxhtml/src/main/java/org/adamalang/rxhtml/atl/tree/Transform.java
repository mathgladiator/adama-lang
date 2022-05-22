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

import java.util.Set;

/** Transform a node */
public class Transform implements Tree {

  public final Tree base;
  public final String operation;

  public Transform(Tree base, String operation) {
    this.base = base;
    this.operation = operation;
  }

  @Override
  public Set<String> variables() {
    return base.variables();
  }

  @Override
  public String debug() {
    return "TRANSFORM(" + base.debug() + "," + operation + ")";
  }

  @Override
  public String js(String env) {
    // TODO: big table
    return operation + "(" + base.js(env) + ")";
  }
}
