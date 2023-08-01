/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.common.Escaping;
import org.adamalang.rxhtml.atl.Context;

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
  public String js(Context context, String env) {
    String textToUse = text;
    if (context.is_class) {
      String trimmed = textToUse.trim();
      context.cssTrack(trimmed);
      // The reason we do this is to ensure developers don't try to make a new class via concatenation as that will break future optimizers.
      textToUse = " " + trimmed + " ";
    }
    return "\"" + new Escaping(textToUse).removeNewLines().keepSlashes().go() + "\"";
  }

  /** silly optimization for empty strings and classes */
  public boolean skip(Context context) {
    if (context.is_class) {
      return text.trim().length() == 0;
    }
    return false;
  }
}
