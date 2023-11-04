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
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
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
  public void format(Formatter formatter) {
    for (PrefixedExpression pe : expressions) {
      pe.expression.format(formatter);
    }
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    return anonymousObject.typingInternal(environment, suggestion);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    anonymousObject.writeJava(sb, environment);
  }

  @Override
  public void free(FreeEnvironment environment) {
    for(var expr : expressions) {
      expr.expression.free(environment);
    }
  }
}
