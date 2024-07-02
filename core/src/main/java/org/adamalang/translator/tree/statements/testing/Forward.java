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
package org.adamalang.translator.tree.statements.testing;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;

import java.util.function.Consumer;

/** march forward time (in seconds) */
public class Forward extends Statement {
  public final Token token;
  public final Expression expression;
  public final Token semicolonToken;

  public Forward(final Token token, final Expression expression, final Token semicolonToken) {
    this.token = token;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    ingest(token);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  @Override
  public void format(Formatter formatter) {
    expression.format(formatter);
  }

  @Override
  public ControlFlow typing(final Environment prior) {
    Environment environment = prior.scopeWithComputeContext(ComputeContext.Computation);
    if (!environment.state.isTesting()) {
      environment.document.createError(this, String.format("Forward is exclusively for testing"));
    }
    environment.rules.IsNumeric(expression.typing(environment, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("__forward(");
    expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(");");
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
