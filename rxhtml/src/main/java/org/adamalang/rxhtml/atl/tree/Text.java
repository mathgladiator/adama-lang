package org.adamalang.rxhtml.atl.tree;

/** Raw Text */
public class Text implements Node {
  public final String text;

  public Text(String text) {
    this.text = text;
  }

  @Override
  public String debug() {
    return "TEXT(" + text + ")";
  }
}
