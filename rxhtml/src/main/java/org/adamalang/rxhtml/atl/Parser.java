package org.adamalang.rxhtml.atl;

import org.adamalang.rxhtml.atl.tree.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Parser {

  // handle transforms
  private static Node wrapTransforms(Node root, TokenStream.Token token) {
    Node n = root;
    for (int k = 0; k < token.transforms.length; k++) {
      n = new Transform(n, token.transforms[k]);
    }
    return n;
  }

  private static Node of(ArrayList<Node> children) {
    if (children.size() > 1) {
      return new Concat(children.toArray(new Node[children.size()]));
    } else if (children.size() == 1){
      return children.get(0);
    } else {
      return new Empty();
    }
  }

  private static Node condition(Iterator<TokenStream.Token> it, TokenStream.Token name) {
    ArrayList<Node> childrenTrue = new ArrayList<>();
    ArrayList<Node> childrenFalse = new ArrayList<>();
    ArrayList<Node> active = childrenTrue;
    while (it.hasNext()) {
      TokenStream.Token token = it.next();
      if (token.type == TokenStream.Type.Condition && token.base.equals(name.base)) {
        if (token.mod == TokenStream.Modifier.Else) {
          active = childrenFalse;
          continue;
        } else if (token.mod == TokenStream.Modifier.End) {
          break;
        }
      } else {
        route(active, it, token);
      }
    }
    Node guard_lookup = wrapTransforms(new Lookup(name.base), name);
    return new Condition(name.mod == TokenStream.Modifier.Not ? new Negate(guard_lookup) : guard_lookup, of(childrenTrue), of(childrenFalse));
  }

  private static void route(ArrayList<Node> children, Iterator<TokenStream.Token> it, TokenStream.Token token) {
    switch (token.type) {
      case Text:
        children.add(new Text(token.base));
        return;
      case Variable:
        children.add(wrapTransforms(new Lookup(token.base), token));
        return;
      case Condition:
        children.add(condition(it, token));
        return;
    }
  }

  private static Node parse(Iterator<TokenStream.Token> it) {
    ArrayList<Node> children = new ArrayList<>();
    while (it.hasNext()) {
      route(children, it, it.next());
    }
    return of(children);
  }

  public static Node parse(String text) {
    return parse(TokenStream.tokenize(text).iterator());
  }
}
