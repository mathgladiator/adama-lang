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
package org.adamalang.translator.tree.statements;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;

import java.util.function.Consumer;

public class Evaluate extends Statement {
  public final Token endToken;
  public final Expression expression;
  public final boolean inForLoop;

  public Evaluate(final Expression expression, final boolean inForLoop, final Token endToken) {
    this.expression = expression;
    this.inForLoop = inForLoop;
    this.endToken = endToken;
    ingest(expression);
    if (endToken != null) {
      ingest(endToken);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    expression.emit(yielder);
    if (endToken != null) {
      yielder.accept(endToken);
    }
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    final var expr = new StringBuilder();
    expression.writeJava(expr, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(expr.toString());
    if (!inForLoop) {
      sb.append(";");
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
