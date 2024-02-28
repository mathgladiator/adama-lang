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
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
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
  public void format(Formatter formatter) {
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    if (how == AlterControlFlowMode.Abort && !(environment.state.isMessageHandler() || environment.state.isAbortable() || environment.state.isAuthorize() || environment.state.isTesting() )) {
      environment.document.createError(this, String.format("Can only 'abort' from a message handler or an abortable procedure/method"));
    }
    if (how == AlterControlFlowMode.Block && !environment.state.isStateMachineTransition()) {
      environment.document.createError(this, String.format("Can only 'block' from a state machine transition"));
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
