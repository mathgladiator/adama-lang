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
  public String js(Context context, String env) {
    return env + "['" + name + "']";
  }
}
