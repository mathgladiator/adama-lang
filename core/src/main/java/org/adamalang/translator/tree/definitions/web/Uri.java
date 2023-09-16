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
package org.adamalang.translator.tree.definitions.web;

import org.adamalang.common.web.UriMatcher;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.Definition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Uri extends Definition {
  private final ArrayList<Consumer<Consumer<Token>>> emission;
  public final TreeMap<String, TyType> variables;
  private final ArrayList<Function<UriTable.UriLevel, UriTable.UriLevel>> next;
  private final StringBuilder str;
  private final StringBuilder rxhtmlPath;
  private final ArrayList<Function<String, Boolean>> matchers;
  private boolean lastHasStar;

  public Uri() {
    this.emission = new ArrayList<>();
    this.variables = new TreeMap<>();
    this.next = new ArrayList<>();
    this.str = new StringBuilder();
    this.rxhtmlPath = new StringBuilder();
    this.matchers = new ArrayList<>();
    this.lastHasStar = false;
  }

  public void push(Token slash, Token dollarSign, Token id, Token starToken, Token colon, TyType type) {
    ingest(slash);
    emission.add((y) -> y.accept(slash));
    str.append("/");
    rxhtmlPath.append("/");
    if (id != null) {
      ingest(id);
      if (dollarSign != null) {
        emission.add((y) -> y.accept(dollarSign));
        str.append("$");
        rxhtmlPath.append("$");
      }
      emission.add((y) -> y.accept(id));
      if (starToken != null) {
        emission.add((y) -> y.accept(starToken));
      }
      String uriFragment = id.stripStringLiteral().text;
      str.append(uriFragment);
      rxhtmlPath.append(uriFragment);
      if (colon != null) {
        ingest(type);
        emission.add((y) -> y.accept(colon));
        emission.add((y) -> type.emit(y));
        variables.put(id.text, type);
        str.append(":");
        str.append(type.getAdamaType());
        rxhtmlPath.append(":");
        switch (type.getAdamaType()) {
          case "int":
          case "double":
          case "long":
            rxhtmlPath.append("number");
            break;
          default:
            rxhtmlPath.append("text");
        }
      }
      if (starToken == null) {
        if (dollarSign != null) {
            if (type instanceof TyNativeBoolean) {
              matchers.add((str) -> str.equals("true") || str.equals("false"));
              next.add((level) -> level.next(id.text, level.bools));
            } else if (type instanceof TyNativeInteger) {
              matchers.add(Uri::isInteger);
              next.add((level) -> level.next(id.text, level.ints));
            } else if (type instanceof TyNativeLong) {
              matchers.add(Uri::isLong);
              next.add((level) -> level.next(id.text, level.longs));
            } else if (type instanceof TyNativeDouble) {
              matchers.add(Uri::isDouble);
              next.add((level) -> level.next(id.text, level.doubles));
            } else if (type instanceof TyNativeString) {
              matchers.add((str) -> true);
              next.add((level) -> level.next(id.text, level.strings));
          }
        } else {
          String fixed = id.stripStringLiteral().text;
          matchers.add((str) -> fixed.equals(str));
          next.add((level) -> level.next(fixed, level.fixed));
        }
      } else {
        lastHasStar = true;
        matchers.add((str) -> true);
        str.append("*");
        variables.put(id.text, new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string")));
        next.add((level) -> level.next(id.text, level.strings).tail());
      }
    } else {
      matchers.add((str) -> "".equals(str));
      next.add((level) -> level.next("", level.fixed));
    }
  }

  public UriMatcher matcher() {
    return new UriMatcher(str.toString(), matchers, lastHasStar);
  }

  public void extendInto(Environment environment) {
    for (Map.Entry<String, TyType> var : variables.entrySet()) {
      environment.define(var.getKey(), var.getValue(), true, this);
    }
  }

  public UriTable.UriLevel dive(UriTable.UriLevel root) {
    UriTable.UriLevel level = root;
    for (Function<UriTable.UriLevel, UriTable.UriLevel> fn : next) {
      level = fn.apply(level);
    }
    return level;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    for (Consumer<Consumer<Token>> emit : emission) {
      emit.accept(yielder);
    }
  }

  public void typing(Environment environment) {
    for (Map.Entry<String, TyType> var : variables.entrySet()) {
      TyType typeToCheck = var.getValue();
      boolean valid = typeToCheck instanceof TyNativeInteger || typeToCheck instanceof TyNativeDouble || typeToCheck instanceof TyNativeLong || typeToCheck instanceof TyNativeString || typeToCheck instanceof TyNativeBoolean;
      if (!valid) {
        environment.document.createError(this, "The parameter type must be int, long, double, string, or boolean");
      }
    }
  }

  @Override
  public String toString() {
    return str.toString();
  }

  public String rxhtmlPath() {
    return this.rxhtmlPath.toString();
  }

  public static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }


  public static boolean isLong(String str) {
    try {
      Long.parseLong(str);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

  public static boolean isDouble(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }
}
