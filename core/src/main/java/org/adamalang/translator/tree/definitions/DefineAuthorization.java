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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/** defines an authorization handler */
public class DefineAuthorization extends Definition {
  public final Token authorize;
  public final Token openParen;
  public final Token username;
  public final Token comma;
  public final Token password;
  public final Token endParen;
  public final Block code;

  public DefineAuthorization(Token authorize, Token openParen, Token username, Token comma, Token password, Token endParen, Block code) {
    this.authorize = authorize;
    this.openParen = openParen;
    this.username = username;
    this.comma = comma;
    this.password = password;
    this.endParen = endParen;
    this.code = code;
    ingest(authorize);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(authorize);
    yielder.accept(openParen);
    yielder.accept(username);
    yielder.accept(comma);
    yielder.accept(password);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsAuthorize();
    TyNativeString tyStr = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, authorize);
    env.define(username.text, tyStr, true, this);
    env.define(password.text, tyStr, true, this);
    return env;
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (env) -> {
      Environment toUse = next(env);
      toUse.setReturnType(new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, authorize));
      ControlFlow flow = code.typing(toUse);
      if (flow == ControlFlow.Open) {
        checker.issueError(this, "@authorize must either return a string or abort");
      }
    });
  }
}
