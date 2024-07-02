/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.operators.Parentheses;

public abstract class LinqExpression extends Expression {
  public final Expression sql;
  protected boolean intermediateExpression;

  public LinqExpression(final Expression sql) {
    intermediateExpression = false;
    this.sql = sql;
    ingest(sql);
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
