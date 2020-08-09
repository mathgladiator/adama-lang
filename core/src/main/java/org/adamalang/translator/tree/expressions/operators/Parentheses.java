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
    final var skip = expression instanceof Parentheses || expression instanceof InlineConditional;
    if (skip) {
      expression.writeJava(sb, environment);
    } else {
      sb.append("(");
      expression.writeJava(sb, environment);
      sb.append(")");
    }
  }
}
