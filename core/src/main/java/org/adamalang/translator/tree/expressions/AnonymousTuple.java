/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AnonymousTuple extends Expression {
  private final AnonymousObject anonymousObject;

  public static class PrefixedExpression {
    public final Token token;
    public final Expression expression;

    public PrefixedExpression(Token token, Expression expression) {
      this.token = token;
      this.expression = expression;
    }
  }

  private final ArrayList<PrefixedExpression> expressions;
  private Token suffix;

  public AnonymousTuple() {
    this.anonymousObject = new AnonymousObject(Token.WRAP("{"));
    this.expressions = new ArrayList<>();
    this.suffix = null;
  }

  public static String nameOf(int priorSize) {
    switch (priorSize) {
      case 0:
        return "first";
      case 1:
        return "second";
      case 2:
        return "third";
      case 3:
        return "fourth";
      case 4:
        return "fifth";
      case 5:
        return "sixth";
      case 6:
        return "seventh";
      case 7:
        return "eighth";
      case 8:
        return "ninth";
      case 9:
        return "tenth";
      default:
        return "pos_" + (priorSize + 1);
    }
  }

  public void add(Token token, Expression expr) {
    Token name = expr.asIdentiferToken(token.sourceName, nameOf(expressions.size()));
    expressions.add(new PrefixedExpression(token, expr));
    anonymousObject.add(token, name, token, expr);
  }

  public void finish(Token token) {
    this.suffix = token;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    for (PrefixedExpression pe : expressions) {
      yielder.accept(pe.token);
      pe.expression.emit(yielder);
    }
    yielder.accept(suffix);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    return anonymousObject.typingInternal(environment, suggestion);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    anonymousObject.writeJava(sb, environment);
  }
}
