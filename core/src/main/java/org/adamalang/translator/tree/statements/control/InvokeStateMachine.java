/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.statements.control;

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

/** invoke a state machine's state; useful for dynamic behavior */
public class InvokeStateMachine extends Statement {
  public final Expression expression;
  public final Token invokeToken;
  public final Token semicolonToken;

  public InvokeStateMachine(final Token invokeToken, final Expression expression, final Token semicolonToken) {
    this.invokeToken = invokeToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    ingest(invokeToken);
    ingest(expression);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(invokeToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var expressionType = expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsStateMachineRef(expressionType, false);
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("__invoke(");
    expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(");");
  }
}
