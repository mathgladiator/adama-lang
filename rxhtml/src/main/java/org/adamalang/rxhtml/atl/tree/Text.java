package org.adamalang.rxhtml.atl.tree;

import java.util.Collections;
import java.util.Set;

/** Raw Text */
public class Text implements Node {
  public final String text;

  public Text(String text) {
    this.text = text;
  }

  @Override
  public Set<String> variables() {
    return Collections.emptySet();
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
