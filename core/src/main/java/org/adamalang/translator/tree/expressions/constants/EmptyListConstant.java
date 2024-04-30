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
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeList;

import java.util.function.Consumer;

/** a simple empty list with a given type */
public class EmptyListConstant extends Expression {
  public final Token emptyListToken;
  public final TokenizedItem<TyType> type;
  private TyType resolved = null;

  public EmptyListConstant(Token emptyListToken, TokenizedItem<TyType> type) {
    this.emptyListToken = emptyListToken;
    this.type = type;
    ingest(emptyListToken);
    ingest(type.item);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(emptyListToken);
    type.emitBefore(yielder);
    type.item.emit(yielder);
    type.emitAfter(yielder);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    resolved = environment.rules.Resolve(type.item, false);
    return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, type);
  }

  @Override
  public void free(FreeEnvironment environment) {}

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    if (resolved != null) {
      sb.append("new EmptyNtList<").append(resolved.getJavaBoxType(environment)).append(">()");
    }
  }
}
