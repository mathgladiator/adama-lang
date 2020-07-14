/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.operators;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.SupportsTwoPhaseTyping;

/** wrap an expression within paranthesis */
public class Parenthesis extends Expression implements SupportsTwoPhaseTyping {
  public final Expression expression;
  public final Token leftParenthesis;
  public final Token rightParenthesis;

  /** @param expression the expression to wrap */
  public Parenthesis(final Token leftParenthesis, final Expression expression, final Token rightParenthesis) {
    this.leftParenthesis = leftParenthesis;
    this.expression = expression;
    this.rightParenthesis = rightParenthesis;
    ingest(leftParenthesis);
    ingest(expression);
    ingest(rightParenthesis);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(leftParenthesis);
    expression.emit(yielder);
    yielder.accept(rightParenthesis);
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
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return expression.typing(environment, suggestion);
  }

  @Override
  public void upgradeType(final Environment environment, final TyType newType) {
    if (expression instanceof SupportsTwoPhaseTyping) {
      ((SupportsTwoPhaseTyping) expression).upgradeType(environment, newType);
    }
    cachedType = newType;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var skip = expression instanceof Parenthesis || expression instanceof InlineConditional;
    if (skip) {
      expression.writeJava(sb, environment);
    } else {
      sb.append("(");
      expression.writeJava(sb, environment);
      sb.append(")");
    }
  }
}
