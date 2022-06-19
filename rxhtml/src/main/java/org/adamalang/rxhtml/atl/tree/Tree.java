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

import java.util.Map;
import java.util.Set;

/** common interface for the tree nodes */
public interface Tree {

  /** return a set of variables within the node */
  public Map<String, String> variables();

  /** turn the node into an easy to debug string */
  public String debug();

  /** javascript expression to build the string */
  public String js(String env);
}
