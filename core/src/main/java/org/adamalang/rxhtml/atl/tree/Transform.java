/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.rxhtml.template.Transforms;

import java.util.Map;

/** Transform a node */
public class Transform implements Tree {

  public final Tree base;
  public final String transform;

  public Transform(Tree base, String transform) {
    this.base = base;
    this.transform = transform;
  }

  @Override
  public Map<String, String> variables() {
    return base.variables();
  }

  @Override
  public String debug() {
    return "TRANSFORM(" + base.debug() + "," + transform + ")";
  }

  @Override
  public String js(String env) {
    return "(" + Transforms.of(transform) + ")(" + base.js(env) + ")";
  }
}
