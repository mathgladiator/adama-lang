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
package org.adamalang.translator.tree.statements.testing;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;

import java.util.function.Consumer;

/**
 * inject a message into a channel; this made sense in the old type system. This should be a
 * function exposed to channel<T>, and only available within a test.
 */
public class PumpMessage extends Statement {
  public final Token channelToken;
  public final Expression expression;
  public final Token intoToken;
  public final Token pumpToken;
  public final Token semiColonToken;
  private TyNativeMessage messageType;

  public PumpMessage(final Token pumpToken, final Expression expression, final Token intoToken, final Token channelToken, final Token semiColonToken) {
    this.pumpToken = pumpToken;
    this.intoToken = intoToken;
    this.channelToken = channelToken;
    this.expression = expression;
    this.semiColonToken = semiColonToken;
    ingest(pumpToken);
    ingest(expression);
    ingest(semiColonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(pumpToken);
    expression.emit(yielder);
    yielder.accept(intoToken);
    yielder.accept(channelToken);
    yielder.accept(semiColonToken);
  }

  @Override
  public void format(Formatter formatter) {
    expression.format(formatter);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var next = environment.scopeWithComputeContext(ComputeContext.Computation);
    if (!next.state.isTesting()) {
      environment.document.createError(this, String.format("Pumping a message is designed exclusively for testing"));
    }
    final var exprType = expression.typing(next, null /* ug */);
    environment.rules.IsNativeMessage(exprType, false);
    if (exprType != null) {
      final var messageNameType = next.document.channelToMessageType.get(channelToken.text);
      if (messageNameType == null) {
        environment.document.createError(this, String.format("Channel '%s' does not exist", channelToken.text));
        return ControlFlow.Open;
      }
      messageType = environment.rules.FindMessageStructure(messageNameType, this, false);
      if (messageType == null) {
        return ControlFlow.Open;
      }
    }
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("__queue.add(new AsyncTask(0, NtPrincipal.NO_ONE, null, \"").append(channelToken.text).append("\", 0, \"origin\", \"ip\", ");
    expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append("));");
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
