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
