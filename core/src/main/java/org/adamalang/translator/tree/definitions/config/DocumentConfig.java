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
package org.adamalang.translator.tree.definitions.config;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;

import java.util.function.Consumer;

/** there are operational parameters which are embedded with the document */
public class DocumentConfig extends StaticPiece {
  public final Token name;
  public final Token equals;
  public final Expression value;
  public final Token semicolon;

  public DocumentConfig(Token name, Token equals, Expression value, Token semicolon) {
    this.name = name;
    this.equals = equals;
    this.value = value;
    this.semicolon = semicolon;
    ingest(name, equals, semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(name);
    yielder.accept(equals);
    value.emit(yielder);
    yielder.accept(semicolon);
  }

  @Override
  public void format(Formatter formatter) {
    value.format(formatter);
  }

  @Override
  public void typing(Environment environment) {
    Environment next = environment.scopeWithComputeContext(ComputeContext.Computation);
    switch (name.text) {
      case "maximum_history":
        next.rules.IsInteger(value.typing(next, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
        return;
      case "delete_on_close":
        next.rules.IsBoolean(value.typing(next, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
        return;
    }
  }
}
