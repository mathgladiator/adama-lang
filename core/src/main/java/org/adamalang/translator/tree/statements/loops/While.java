/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.statements.loops;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

/** classical while(condition) {...} loop */
public class While extends Statement {
  public final Block code;
  public final Expression condition;
  public final Token endParen;
  public final Token openParen;
  public final Token whileToken;

  public While(final Token whileToken, final Token openParen, final Expression condition, final Token endParen, final Block code) {
    this.whileToken = whileToken;
    ingest(whileToken);
    this.openParen = openParen;
    this.condition = condition;
    this.endParen = endParen;
    this.code = code;
    ingest(condition);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(whileToken);
    yielder.accept(openParen);
    condition.emit(yielder);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var conditionType = condition.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsBoolean(conditionType, false);
    code.typing(environment.scope());
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.state.isStatic()) {
      sb.append("while (__static_state.__goodwill(").append(condition.toArgs(true)).append(") && (");
    } else {
      sb.append("while (__goodwill(").append(condition.toArgs(true)).append(") && (");
    }
    condition.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(")) ");
    code.writeJava(sb, environment.scope());
  }

  @Override
  public void free(FreeEnvironment environment) {
    condition.free(environment);
    code.free(environment.push());
  }
}
