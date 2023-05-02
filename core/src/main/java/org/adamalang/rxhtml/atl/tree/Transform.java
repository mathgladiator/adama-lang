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
