/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.operators.Parenthesis;

public abstract class LinqExpression extends Expression {
  protected boolean intermediateExpression;
  public final Expression sql;

  public LinqExpression(final Expression sql) {
    intermediateExpression = false;
    this.sql = sql;
    indicateIntermediateExpression(sql);
  }

  protected void indicateIntermediateExpression(final Expression expression) {
    if (expression != null) {
      if (expression instanceof Parenthesis) {
        indicateIntermediateExpression(((Parenthesis) expression).expression);
      } else if (expression instanceof LinqExpression) {
        ((LinqExpression) expression).intermediateExpression = true;
      }
    }
  }
}
