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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

public class DefineTrafficHint extends Definition {
  public final Token trafficToken;
  public final Expression expression;
  public final Token semicolon;

  public DefineTrafficHint(Token trafficToken, Expression expression, Token semicolon) {
    this.trafficToken = trafficToken;
    this.expression = expression;
    this.semicolon = semicolon;
    ingest(trafficToken);
    ingest(semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(trafficToken);
    expression.emit(yielder);
    yielder.accept(semicolon);
  }

  @Override
  public void format(Formatter formatter) {
    expression.format(formatter);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    expression.free(fe);
    checker.register(fe.free, (environment) -> {
      TyType exprType = expression.typing(next(environment), new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(this));
      environment.rules.IsString(exprType, false);
    });
  }

  public Environment next(Environment environment) {
    return environment.scopeWithComputeContext(ComputeContext.Computation).scopeAsReadOnlyBoundary().scopeTrafficHint();
  }
}
