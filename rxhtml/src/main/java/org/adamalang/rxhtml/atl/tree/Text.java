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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** Raw Text */
public class Text implements Tree {
  public final String text;

  public Text(String text) {
    this.text = text;
  }

  @Override
  public Map<String, String>  variables() {
    return Collections.emptyMap();
  }

  @Override
  public String debug() {
    return "TEXT(" + text + ")";
  }

  @Override
  public String js(String env) {
    return "\"" + text + "\""; // BIG TODO: escaping
  }
}
