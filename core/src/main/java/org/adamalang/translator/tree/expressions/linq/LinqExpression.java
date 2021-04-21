/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.operators.Parentheses;

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
      if (expression instanceof Parentheses) {
        indicateIntermediateExpression(((Parentheses) expression).expression);
      } else if (expression instanceof LinqExpression) {
        ((LinqExpression) expression).intermediateExpression = true;
      }
    }
  }
}
