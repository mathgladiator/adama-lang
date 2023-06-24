/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.template.tree;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.common.template.Settings;

import java.util.ArrayList;

/** concat multiple T together */
public class TConcat implements T {
  private final ArrayList<T> children;

  public TConcat() {
    this.children = new ArrayList<>();
  }

  public void add(T child) {
    this.children.add(child);
  }

  @Override
  public void render(Settings settings, JsonNode node, StringBuilder output) {
    for (T child : children) {
      child.render(settings, node, output);
    }
  }
}
