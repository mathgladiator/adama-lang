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

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.operands.PrefixMutateOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanBumpResult;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.function.Consumer;

/** prefix mutation ($e--, $e++) and prefix change (!, -) */
public class PrefixMutate extends Expression {
  public final Expression expression;
  public final PrefixMutateOp op;
  public final Token opToken;
  private final boolean addGet;
  private CanBumpResult bumpResult;

  public PrefixMutate(final Expression expression, final Token opToken) {
    this.expression = expression;
    this.opToken = opToken;
    op = PrefixMutateOp.fromText(opToken.text);
    ingest(opToken);
    ingest(expression);
    addGet = false;
    bumpResult = CanBumpResult.No;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(opToken);
    expression.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    expression.format(formatter);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var newContext = op.requiresAssignment ? ComputeContext.Assignment : ComputeContext.Computation;
    TyType result = null;
    if (op == PrefixMutateOp.BumpUp || op == PrefixMutateOp.BumpDown) {
      result = expression.typing(environment.scopeWithComputeContext(newContext), null);
      bumpResult = environment.rules.CanBumpNumeric(result, false);
    } else if (op == PrefixMutateOp.NegateNumber) {
      result = expression.typing(environment.scopeWithComputeContext(newContext), null);
      bumpResult = environment.rules.CanBumpNumeric(result, false);
    } else if (op == PrefixMutateOp.NegateBool) {
      result = expression.typing(environment.scopeWithComputeContext(newContext), null);
      bumpResult = environment.rules.CanBumpBool(result, false);
    }
    if (bumpResult == CanBumpResult.No) {
      return null;
    }
    if (result instanceof DetailComputeRequiresGet && bumpResult.reactive) {
      return ((DetailComputeRequiresGet) result).typeAfterGet(environment).makeCopyWithNewPosition(this, result.behavior);
    }
    return result.makeCopyWithNewPosition(this, result.behavior);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var newContext = op.requiresAssignment ? ComputeContext.Assignment : ComputeContext.Computation;
    switch (bumpResult) {
      case YesWithNative:
        sb.append(op.javaOp);
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        break;
      case YesWithSetter:
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        sb.append(op.functionCall);
        break;
      case YesWithListTransformSetter:
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        sb.append(".transform((item) -> item").append(op.functionCall).append(")");
        break;
      case YesWithListTransformNative:
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        sb.append(".transform((item) -> ").append(op.javaOp).append("item)");
        break;
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
