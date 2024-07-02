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
package org.adamalang.translator.tree.definitions.web;

import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.privacy.Guard;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.Collections;
import java.util.function.Consumer;

public class WebGuard extends DocumentPosition {
  public final Token requires;
  public final Guard guard;

  public WebGuard(Token requires, Guard guard) {
    this.requires = requires;
    this.guard = guard;
    ingest(requires);
    ingest(guard);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(requires);
    guard.emit(yielder);
  }

  public void format(Formatter formatter) {
    guard.format(formatter);
  }

  public void typing(TypeCheckerRoot checker) {
    checker.register(Collections.emptySet(), (env) -> {
      for (TokenizedItem<String> policy : guard.policies) {
        var dcp = env.document.root.storage.policies.get(policy.item);
        if (dcp == null) {
          env.document.createError(this, String.format("Policy '%s' was not found for web operation", policy.item));
        }
      }
    });
  }
}
