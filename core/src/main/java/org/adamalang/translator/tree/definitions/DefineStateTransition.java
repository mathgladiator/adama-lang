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

import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/**
 * A document is in a variety of states, and as such we need code to run in each state; this is a
 * state transition that maps which code to run in a specific state.
 */
public class DefineStateTransition extends Definition {
  public final String name;
  public final Token nameToken;
  public Block code;

  public DefineStateTransition(final Token nameToken, final Block code) {
    this.nameToken = nameToken;
    name = nameToken.text.substring(1);
    this.code = code;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(nameToken);
    code.emit(yielder);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (env) -> code.typing(env.scopeAsStateMachineTransition()));
  }
}
