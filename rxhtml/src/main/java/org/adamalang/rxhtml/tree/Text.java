package org.adamalang.rxhtml.tree;

import org.adamalang.translator.parser.token.Token;

public class Text implements Item {
  public final Token token;

  public Text(Token text) {
    this.token = text;
  }

  @Override
  public void dump(int depth) {
    System.out.println(depth + ":token:" + token.text);
  }

  @Override
  public boolean varies() {
    if  (token.isStringLiteral() || token.isNumberLiteralDouble() || token.isNumberLiteralInteger()) {
      return false;
    }
    return true;
  }

  @Override
  public String html() {
    // TODO: evaluate the escaped text
    return token.text;
  }
}
