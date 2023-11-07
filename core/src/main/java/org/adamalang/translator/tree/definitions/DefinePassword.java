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
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/** a very special way for a user of a document to set their password */
@Deprecated
public class DefinePassword extends Definition {
  public final Token passwordToken;
  public final Token openParen;
  public final Token passwordVar;
  public final Token endParen;
  public final Block code;

  public DefinePassword(Token passwordToken, Token openParen, Token passwordVar, Token endParen, Block code) {
    this.passwordToken = passwordToken;
    this.openParen = openParen;
    this.passwordVar = passwordVar;
    this.endParen = endParen;
    this.code = code;
    ingest(passwordToken);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(passwordToken);
    yielder.accept(openParen);
    yielder.accept(passwordVar);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(passwordToken);
    code.format(formatter);
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsPolicy();
    TyNativeString tyStr = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, passwordToken);
    env.define(passwordVar.text, tyStr, true, this);
    return env;
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (env) -> {
      code.typing(next(env));
    });
  }
}
