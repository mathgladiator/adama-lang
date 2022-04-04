package org.adamalang.rxhtml.tree;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;

public class Node implements Item {

  private final TokenizedItem<Token> open;
  public final Attribute[] attributes;
  public final Item[] children;
  private final TokenizedItem<Token> close;
  private final boolean varies;

  public final String tag;

  public Node(TokenizedItem<Token> open, Attribute[] attributes, Item[] children, TokenizedItem<Token> close) {
    this.open = open;
    this.tag = open.item.text;
    this.attributes = attributes;
    this.children = children;
    this.close = close;
    boolean doesVary = false;
    for (Attribute attribute : attributes) {
      if (attribute.guards.length != 0) {
        doesVary = true;
      }
    }
    for (Item child : children) {
      if (child.varies()) {
        doesVary = true;
      }
    }
    this.varies = doesVary;
  }

  @Override
  public void dump(int depth) {
    System.out.println(depth + ":node:" + open.item.text);
    for (Attribute attribute : attributes) {
      System.out.println(attribute.name.text + "=" + (attribute.value != null ? attribute.value.text : "<null>"));
    }
    for (Item child : children) {
      child.dump(depth + 1);
    }
  }

  @Override
  public boolean varies() {
    return varies;
  }

  @Override
  public String html() {
    StringBuilder sb = new StringBuilder();
    sb.append("<").append(open.item.text);
    for (Attribute attribute : attributes) {
      sb.append(" ").append(attribute.html());
    }
    sb.append(">");
    for (Item child : children) {
      sb.append(child.html());
    }
    sb.append("</").append(open.item.text).append(">");
    return sb.toString();
  }
}
