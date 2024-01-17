/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

  private static boolean isAutoVar(String name) {
    return ("%".equals(name) || "$".equals(name) || "#".equals(name));
  }

  private static Tree getBaseOf(TokenStream.Token conditionStart) throws ParseException {
    // TODO: && and ||
    for (String operator : Operate.OPERATORS) {
      if (conditionStart.base.contains(operator)) {
        String[] parts = conditionStart.base.split(Pattern.quote(operator));
        return new Operate(new Lookup(parts[0].trim()), parts.length > 1 ? parts[1].trim() : "", Operate.convertOp(operator));
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
        if (isAutoVar(token.base)) {
          children.add(new AutoVar());
        } else {
          children.add(wrapTransforms(new Lookup(token.base), token));
        }
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
