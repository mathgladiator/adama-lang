package org.adamalang.rxhtml.tree;

import org.adamalang.translator.parser.token.Token;

public class Text implements Item {
  public final Token text;

  public Text(Token text) {
    this.text = text;
  }

  @Override
  public void dump(int depth) {
    System.out.println(depth + ":text:" + text.text);
  }

  @Override
  public boolean varies() {
    return false;
  }


  @Override
  public String html() {
    return text.text;
  }
}
