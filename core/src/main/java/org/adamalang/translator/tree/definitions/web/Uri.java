/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions.web;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.Definition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Uri extends Definition {
  private final ArrayList<Consumer<Consumer<Token>>> emission;
  private final TreeMap<String, TyType> variables;
  private final ArrayList<Function<UriTable.UriLevel, UriTable.UriLevel>> next;
  private final StringBuilder str;

  public Uri() {
    this.emission = new ArrayList<>();
    this.variables = new TreeMap<>();
    this.next = new ArrayList<>();
    this.str = new StringBuilder();
  }

  public void push(Token slash, Token dollarSign, Token id, Token starToken, Token colon, TyType type) {
    ingest(slash);
    emission.add((y) -> y.accept(slash));
    str.append("/");
    if (id != null) {
      ingest(id);
      if (dollarSign != null) {
        emission.add((y) -> y.accept(dollarSign));
        str.append("$");
      }
      emission.add((y) -> y.accept(id));
      if (starToken != null) {
        emission.add((y) -> y.accept(starToken));
      }
      str.append(id.text);
      if (colon != null) {
        ingest(type);
        emission.add((y) -> y.accept(colon));
        emission.add((y) -> type.emit(y));
        variables.put(id.text, type);
        str.append(":");
        str.append(type.getAdamaType());
      }
      if (starToken == null) {
        if (dollarSign != null) {
            if (type instanceof TyNativeBoolean) {
              next.add((level) -> level.next(id.text, level.bools));
            } else if (type instanceof TyNativeInteger) {
              next.add((level) -> level.next(id.text, level.ints));
            } else if (type instanceof TyNativeLong) {
              next.add((level) -> level.next(id.text, level.longs));
            } else if (type instanceof TyNativeDouble) {
              next.add((level) -> level.next(id.text, level.doubles));
            } else if (type instanceof TyNativeString) {
              next.add((level) -> level.next(id.text, level.strings));
          }
        } else {
          next.add((level) -> level.next(id.text, level.fixed));
        }
      } else {
        next.add((level) -> level.next(id.text, level.strings).tail());
      }
    }
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

  @Override
  public void typing(Environment environment) {
    for (Map.Entry<String, TyType> var : variables.entrySet()) {
      TyType typeToCheck = var.getValue();
      boolean valid = typeToCheck instanceof TyNativeInteger || typeToCheck instanceof TyNativeDouble || typeToCheck instanceof TyNativeLong || typeToCheck instanceof TyNativeString || typeToCheck instanceof TyNativeBoolean;
      if (!valid) {
        environment.document.createError(this, "The parameter type must be int, long, double, string, or boolean", "WebUri");
      }
    }
  }

  @Override
  public String toString() {
    return str.toString();
  }
}
