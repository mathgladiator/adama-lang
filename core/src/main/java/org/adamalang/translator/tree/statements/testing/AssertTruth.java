/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.statements.testing;

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

/** Asserts a true statement. This is useful for testing, but also measuring
 * quality as things go bump in the night. */
public class AssertTruth extends Statement {
  public final Token assertToken;
  public final Expression expression;
  public final Token semiColonToken;

  public AssertTruth(final Token assertToken, final Expression expression, final Token semiColonToken) {
    this.assertToken = assertToken;
    this.expression = expression;
    this.semiColonToken = semiColonToken;
    ingest(assertToken);
    ingest(expression);
    ingest(semiColonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(assertToken);
    expression.emit(yielder);
    yielder.accept(semiColonToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var expressionType = expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsBoolean(expressionType, false);
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("__assert_truth(");
    expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(toArgs(false));
    sb.append(");");
  }
}
