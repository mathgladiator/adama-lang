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

/** common interface for the tree nodes */
public interface Tree {

  /** return a set of variables within the node */
  Map<String, String> variables();

  /** turn the node into an easy to debug string */
  String debug();

  /** javascript expression to build the string */
  String js(String env);
}
