/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;

import java.util.function.Consumer;

/** return from the current function (and maybe with a value) */
public class Return extends Statement {
  public final Expression expression;
  public final Token returnToken;
  public final Token semicolonToken;

  public Return(final Token returnToken, final Expression expression, final Token semicolonToken) {
    this.returnToken = returnToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    ingest(returnToken);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(returnToken);
    if (expression != null) {
      expression.emit(yielder);
    }
    yielder.accept(semicolonToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var expectedReturnType = environment.getMostRecentReturnType();
    if (expression != null) {
      if (expectedReturnType != null) {
        final var givenReturnType =
            expression.typing(
                environment.scopeWithComputeContext(ComputeContext.Computation),
                expectedReturnType);
        if (!environment.rules.CanTypeAStoreTypeB(
            expectedReturnType, givenReturnType, StorageTweak.None, false)) {
          return ControlFlow.Open;
        }
      } else {
        environment.document.createError(
            this, String.format("The return statement expects no expression"), "ReturnFlow");
      }
    } else if (expectedReturnType != null) {
      environment.document.createError(
          this,
          String.format(
              "The return statement expected an expression of type `%s`",
              expectedReturnType.getAdamaType()),
          "ReturnFlow");
    }
    return ControlFlow.Returns;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("return");
    if (expression != null) {
      sb.append(" ");
      expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    }
    sb.append(";");
  }
}
