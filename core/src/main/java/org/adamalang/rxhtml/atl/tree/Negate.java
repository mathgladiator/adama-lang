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

/** Negate a node (i.e. not for boolean) */
public class Negate implements Tree {
  private final Tree value;

  public Negate(Tree value) {
    this.value = value;
  }

  @Override
  public Map<String, String> variables() {
    return value.variables();
  }

  @Override
  public String debug() {
    return "!(" + value.debug() + ")";
  }

  @Override
  public String js(String env) {
    return "!(" + value.js(env) + ")";
  }
}
