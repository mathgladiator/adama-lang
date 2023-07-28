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

import java.util.Map;

/** common interface for the tree nodes */
public interface Tree {

  /** return a set of variables within the node */
  Map<String, String> variables();

  /** turn the node into an easy to debug string */
  String debug();

  /** javascript expression to build the string */
  String js(Context context, String env);
}
