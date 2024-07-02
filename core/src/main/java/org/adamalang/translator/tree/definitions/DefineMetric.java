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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/** define a metric to emit a numeric value periodically */
public class DefineMetric extends Definition {
  public final Token metricToken;
  public final Token nameToken;
  public final Token equalsToken;
  public final Expression expression;
  public final Token semicolonToken;
  public TyType metricType;

  public DefineMetric(Token metricToken, Token nameToken, Token equalsToken, Expression expression, Token semicolonToken) {
    this.metricToken = metricToken;
    this.nameToken = nameToken;
    this.equalsToken = equalsToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    this.metricType = null;
    ingest(semicolonToken);
    ingest(metricToken);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(metricToken);
    yielder.accept(nameToken);
    yielder.accept(equalsToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(metricToken);
    expression.format(formatter);
    formatter.endLine(semicolonToken);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    expression.free(fe);
    checker.register(fe.free, (environment) -> {
      Environment next = environment.scopeWithComputeContext(ComputeContext.Computation).scopeAsReadOnlyBoundary();
      metricType = expression.typing(next, new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(this));
      // we only support numeric types (for now))
      boolean good = environment.rules.IsLong(metricType, true) || environment.rules.IsNumeric(metricType, true);
      if (!good && metricType != null) {
        environment.document.createError(this, String.format("Type check failure: must have a type of int, long, or double; instead, but the type is actually '%s'", metricType.getAdamaType()));
      }
    });
  }
}
