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
import java.util.TreeMap;

/** an if/then/else node */
public class Condition implements Tree {
  public final Tree guard;
  public Tree branchTrue;
  public Tree branchFalse;

  public Condition(Tree guard, Tree branchTrue, Tree branchFalse) {
    this.guard = guard;
    this.branchTrue = branchTrue;
    this.branchFalse = branchFalse;
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    union.putAll(guard.variables());
    union.putAll(branchTrue.variables());
    union.putAll(branchFalse.variables());
    return union;
  }

  @Override
  public String debug() {
    return "(" + guard.debug() + ") ? (" + branchTrue.debug() + ") : (" + branchFalse.debug() + ")";
  }

  @Override
  public String js(String env) {
    return "((" + guard.js(env) + ") ? (" + branchTrue.js(env) + ") : (" + branchFalse.js(env) + "))";
  }
}
