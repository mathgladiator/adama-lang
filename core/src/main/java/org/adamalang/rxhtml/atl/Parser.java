/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import org.adamalang.rxhtml.atl.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class Parser {

  // handle transforms
  private static Tree wrapTransforms(Tree root, TokenStream.Token token) {
    Tree n = root;
    for (int k = 0; k < token.transforms.length; k++) {
      n = new Transform(n, token.transforms[k]);
    }
    return n;
  }

  private static Tree of(ArrayList<Tree> children) {
    if (children.size() > 1) {
      return new Concat(children.toArray(new Tree[children.size()]));
    } else if (children.size() == 1) {
      return children.get(0);
    } else {
      return new Empty();
    }
  }

  private static Tree getBaseOf(TokenStream.Token conditionStart) throws ParseException {
    for (String operator : Operate.OPERATORS) {
      if (conditionStart.base.contains(operator)) {
        String[] parts = conditionStart.base.split(Pattern.quote(operator));
        return new Operate(new Lookup(parts[0].trim()), parts[1].trim(), Operate.convertOp(operator));
      }
    }
    return new Lookup(conditionStart.base);
  }

  private static Tree condition(Iterator<TokenStream.Token> it, TokenStream.Token conditionStart) throws ParseException {
    ArrayList<Tree> childrenTrue = new ArrayList<>();
    ArrayList<Tree> childrenFalse = new ArrayList<>();
    ArrayList<Tree> active = childrenTrue;

    while (true) {
      if (!it.hasNext()) {
        throw new ParseException("unclosed condition block");
      }
      TokenStream.Token token = it.next();
      if (token.type == TokenStream.Type.Condition) {
        if (token.mod == TokenStream.Modifier.None) {
          route(active, it, token);
        } else if (token.mod == TokenStream.Modifier.Else) {
          active = childrenFalse;
        } else if (token.mod == TokenStream.Modifier.End) {
          break;
        }
      } else {
        route(active, it, token);
      }
    }


    Tree lookup = getBaseOf(conditionStart);
    Tree guard = wrapTransforms(lookup, conditionStart);
    if (conditionStart.mod == TokenStream.Modifier.Not) {
      guard = new Negate(guard);
    }
    return new Condition(guard, of(childrenTrue), of(childrenFalse));
  }

  private static void route(ArrayList<Tree> children, Iterator<TokenStream.Token> it, TokenStream.Token token) throws ParseException {
    switch (token.type) {
      case Text:
        children.add(new Text(token.base));
        return;
      case Variable:
        children.add(wrapTransforms(new Lookup(token.base), token));
        return;
      case Condition:
        children.add(condition(it, token));
    }
  }

  private static Tree parse(Iterator<TokenStream.Token> it) throws ParseException {
    ArrayList<Tree> children = new ArrayList<>();
    while (it.hasNext()) {
      route(children, it, it.next());
    }
    return of(children);
  }

  public static Tree parse(String text) throws ParseException {
    return parse(TokenStream.tokenize(text).iterator());
  }
}
