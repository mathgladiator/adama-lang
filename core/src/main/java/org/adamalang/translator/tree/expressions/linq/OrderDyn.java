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
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

import java.util.function.Consumer;

/** order the given sql expression result by a string containing dynamic compare instructions */
public class OrderDyn extends LinqExpression {
  public final Token dynOrderToken;
  private TyReactiveRecord elementType;
  private final Expression expr;

  public OrderDyn(final Expression sql, final Token dynOrderToken, final Expression expr) {
    super(sql);
    this.dynOrderToken = dynOrderToken;
    this.expr = expr;
    ingest(sql);
    ingest(expr);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(dynOrderToken);
    expr.emit(yielder);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    TyType base = sql.typing(environment, suggestion);
    TyType str = expr.typing(environment, new TyNativeString(TypeBehavior.ReadWriteWithSetGet, null, dynOrderToken));
    environment.rules.IsString(str, false);
    if (environment.rules.IsNativeListOfStructure(base, false)) {
      TyType embedType = ((DetailContainsAnEmbeddedType) base).getEmbeddedType(environment);
      if (embedType instanceof TyReactiveRecord) {
        elementType = (TyReactiveRecord) embedType;
      } else {
        environment.document.createError(this, "order_dyn requires the list to contain reactive records");
      }
      return base;
    }
    return null;
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    expr.free(environment);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    sql.writeJava(sb, environment);
    sb.append(".orderBy(").append(intermediateExpression ? "false" : "true").append(",new DynCmp_RTx").append(elementType.name).append("(");
    expr.writeJava(sb, environment);
    sb.append("))");
  }
}
