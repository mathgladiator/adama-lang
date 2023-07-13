/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import java.util.Collections;
import java.util.Map;

/** Raw Text */
public class Text implements Tree {
  public final String text;

  public Text(String text) {
    this.text = text;
  }

  @Override
  public Map<String, String> variables() {
    return Collections.emptyMap();
  }

  @Override
  public String debug() {
    return "TEXT(" + text + ")";
  }

  @Override
  public String js(String env) {
    return "\"" + text.replaceAll("[\n|\r]", "") + "\""; // BIG TODO: escaping
  }
}
