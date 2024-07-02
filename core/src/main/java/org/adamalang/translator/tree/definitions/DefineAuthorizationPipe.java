/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/** defines an authorization handler */
public class DefineAuthorizationPipe extends Definition {
  public final Token authorize;
  public final Token openParen;
  public final Token messageType;
  public final Token messageValue;
  public final Token endParen;
  public final Block code;

  public DefineAuthorizationPipe(Token authorize, Token openParen, Token messageType, Token messageValue, Token endParen, Block code) {
    this.authorize = authorize;
    this.openParen = openParen;
    this.messageType = messageType;
    this.messageValue = messageValue;
    this.endParen = endParen;
    this.code = code;
    ingest(authorize);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(authorize);
    yielder.accept(openParen);
    yielder.accept(messageType);
    yielder.accept(messageValue);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(authorize);
    code.format(formatter);
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsAuthorize();
    TyType type = env.rules.Resolve(env.document.types.get(messageType.text), false);
    env.rules.IsNativeMessage(type, false);
    if (type != null) {
      env.define(messageValue.text, type, true, this);
    }
    return env;
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (env) -> {
      Environment toUse = next(env);
      ControlFlow flow = code.typing(toUse);
      if (flow == ControlFlow.Open) {
        checker.issueError(this, "@authorization must either return a special message");
      }
    });
  }
}
