/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

public class AlterControlFlow extends Statement {
  public final AlterControlFlowMode how;
  public final Token semicolonToken;
  public final Token token;

  public AlterControlFlow(final Token token, final AlterControlFlowMode how, final Token semicolonToken) {
    this.token = token;
    this.how = how;
    this.semicolonToken = semicolonToken;
    ingest(token);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
    yielder.accept(semicolonToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    if (how == AlterControlFlowMode.Abort && !(environment.state.isMessageHandler() || environment.state.isAbortable() || environment.state.isAuthorize() )) {
      environment.document.createError(this, String.format("Can only 'abort' from a message handler or an abortable procedure/method"), "EvaluationContext");
    }
    if (how == AlterControlFlowMode.Block && !environment.state.isStateMachineTransition()) {
      environment.document.createError(this, String.format("Can only 'block' from a state machine transition"), "EvaluationContext");
    }
    if (how == AlterControlFlowMode.Abort || how == AlterControlFlowMode.Block) {
      return ControlFlow.Returns;
    }
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (how == AlterControlFlowMode.Break) {
      sb.append("break;");
    } else if (how == AlterControlFlowMode.Continue) {
      sb.append("continue;");
    } else if (how == AlterControlFlowMode.Abort) {
      sb.append("throw new AbortMessageException();");
    } else if (how == AlterControlFlowMode.Block) {
      sb.append("throw new ComputeBlockedException(null, null);");
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
