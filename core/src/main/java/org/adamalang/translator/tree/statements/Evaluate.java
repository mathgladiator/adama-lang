/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
