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

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.Collections;
import java.util.function.Consumer;

public class AugmentViewerState extends Definition {
  public final Token viewToken;
  public final TyType type;
  public final Token name;
  public final Token semicolon;

  public AugmentViewerState(Token viewToken, TyType type, Token name, Token semicolon) {
    this.viewToken = viewToken;
    this.type = type;
    this.name = name;
    this.semicolon = semicolon;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(viewToken);
    type.emit(yielder);
    yielder.accept(name);
    yielder.accept(semicolon);
  }

  public void typing(TypeCheckerRoot checker) {
    checker.define(name.cloneWithNewText("viewer:" + name.text), Collections.EMPTY_SET, (env) -> {
      TyType resolved = env.rules.Resolve(type, false);
      if (resolved != null) {
        resolved.typing(env);
      }
    });
  }
}
