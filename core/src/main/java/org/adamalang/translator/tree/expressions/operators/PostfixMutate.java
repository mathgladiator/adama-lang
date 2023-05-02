/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.operands.PostfixMutateOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanBumpResult;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.function.Consumer;

/** postfix mutation ($e--, $e++) */
public class PostfixMutate extends Expression {
  public final Expression expression;
  public final PostfixMutateOp op;
  public final Token opToken;
  private CanBumpResult bumpResult;

  public PostfixMutate(final Expression expression, final Token opToken) {
    this.expression = expression;
    this.opToken = opToken;
    op = PostfixMutateOp.fromText(opToken.text);
    ingest(expression);
    ingest(opToken);
    bumpResult = CanBumpResult.No;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    expression.emit(yielder);
    yielder.accept(opToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var result = expression.typing(environment.scopeWithComputeContext(ComputeContext.Assignment), null /* no suggestion makes sense */);
    bumpResult = environment.rules.CanBumpNumeric(result, false);
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
    expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
    switch (bumpResult) {
      case YesWithNative:
        sb.append(op.javaOp);
        break;
      case YesWithSetter:
        sb.append(op.functionCall);
        break;
      case YesWithListTransformSetter:
        sb.append(".transform((item) -> item").append(op.functionCall).append(")");
        break;
      case YesWithListTransformNative:
        environment.rules.ExtractEmbeddedType(cachedType, false);
        sb.append(".transform((item) -> item").append(op.javaOp).append(")");
        return;
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
