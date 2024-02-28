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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;

import java.util.function.Consumer;

public class Send extends Statement {
  private final Token send;
  private final Token channel;
  private final Token open;
  private final Expression who;
  private final Token comma;
  private final Expression message;
  private final Token close;

  public Send(Token send, Token channel, Token open, Expression who, Token comma, Expression message, Token close) {
    this.send = send;
    this.channel = channel;
    this.open = open;
    this.who = who;
    this.comma = comma;
    this.message = message;
    this.close = close;
    ingest(send);
    ingest(channel);
    ingest(close);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(send);
    yielder.accept(channel);
    yielder.accept(open);
    who.emit(yielder);
    yielder.accept(comma);
    message.emit(yielder);
    yielder.accept(close);
  }

  @Override
  public void format(Formatter formatter) {
    who.format(formatter);
    message.format(formatter);
  }

  @Override
  public ControlFlow typing(Environment environment) {
    final var next = environment.scopeWithComputeContext(ComputeContext.Computation);
    if (!next.state.isTesting()) {
      environment.document.createError(this, String.format("@send is for testing purposes only"));
    }
    final var whoType = who.typing(next, new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, null));
    environment.rules.IsPrincipal(whoType, false);
    final var exprType = message.typing(next, null /* ug */);
    environment.rules.IsNativeMessage(exprType, false);
    if (exprType != null) {
      final var messageNameType = next.document.channelToMessageType.get(channel.text);
      if (messageNameType == null) {
        environment.document.createError(this, String.format("Channel '%s' does not exist", channel.text));
        return ControlFlow.Open;
      }
      TyType messageType = environment.rules.FindMessageStructure(messageNameType, this, false);
      if (messageType != null) {
        environment.rules.CanTypeAStoreTypeB(messageType, exprType, StorageTweak.None, false);
      }
    }
    return ControlFlow.Open;
  }

  @Override
  public void free(FreeEnvironment environment) {
    who.free(environment);
    message.free(environment);
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    String whoVar = "__who_" + environment.autoVariable();
    sb.append("NtPrincipal ").append(whoVar).append(" = ");
    who.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(";").writeNewline();
    sb.append("__test_send(\"").append(channel.text).append("\",").append(whoVar).append(",");
    message.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(");").writeNewline();
    sb.append("__test_progress();");
  }
}
