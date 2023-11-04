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
package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.SupportsTwoPhaseTyping;

import java.util.function.Consumer;

/** wrap an expression within parentheses */
public class Parentheses extends Expression implements SupportsTwoPhaseTyping {
  public final Expression expression;
  public final Token leftParentheses;
  public final Token rightParentheses;

  /** @param expression the expression to wrap */
  public Parentheses(final Token leftParentheses, final Expression expression, final Token rightParentheses) {
    this.leftParentheses = leftParentheses;
    this.expression = expression;
    this.rightParentheses = rightParentheses;
    ingest(leftParentheses);
    ingest(expression);
    ingest(rightParentheses);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(leftParentheses);
    expression.emit(yielder);
    yielder.accept(rightParentheses);
  }

  @Override
  public void format(Formatter formatter) {
    expression.format(formatter);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return expression.typing(environment, suggestion);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var skip = expression instanceof Parentheses || expression instanceof InlineConditional;
    if (skip) {
      expression.writeJava(sb, environment);
    } else {
      sb.append("(");
      expression.writeJava(sb, environment);
      sb.append(")");
    }
  }

  @Override
  public TyType estimateType(final Environment environment) {
    if (expression instanceof SupportsTwoPhaseTyping) {
      return ((SupportsTwoPhaseTyping) expression).estimateType(environment);
    } else {
      return expression.typing(environment, null);
    }
  }

  @Override
  public void upgradeType(final Environment environment, final TyType newType) {
    if (expression instanceof SupportsTwoPhaseTyping) {
      ((SupportsTwoPhaseTyping) expression).upgradeType(environment, newType);
    }
    cachedType = newType;
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
