/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.rxhtml.template.StatePath;

import java.util.Collections;
import java.util.Map;

/** lookup a variable */
public class Lookup implements Tree {
  public final String name;
  public final String complete;

  public Lookup(String variable) {
    StatePath resolve = StatePath.resolve(variable, "$");
    this.name = resolve.name;
    this.complete = variable;
  }

  @Override
  public Map<String, String> variables() {
    return Collections.singletonMap(name, complete);
  }

  @Override
  public String debug() {
    return "LOOKUP[" + name + "]";
  }

  @Override
  public String js(String env) {
    return env + "['" + name + "']";
  }
}
