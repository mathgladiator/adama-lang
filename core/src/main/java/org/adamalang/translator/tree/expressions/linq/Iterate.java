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
import org.adamalang.translator.tree.types.natives.TyNativeList;

import java.util.function.Consumer;

/** begin a linq query to convert a table into a list which may be filtered, ordered, limited */
public class Iterate extends LinqExpression {
  public final Expression expression;
  public final Token selectToken;

  public Iterate(final Token selectToken, final Expression expression) {
    super(null);
    this.selectToken = selectToken;
    this.expression = expression;
    ingest(selectToken);
    ingest(expression);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(selectToken);
    expression.emit(yielder);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var exprType = expression.typing(environment, null /* no suggestion makes sense */);
    if (exprType != null && environment.rules.IsTable(exprType, false)) {
      final var recordType = environment.rules.ExtractEmbeddedType(exprType, false);
      if (recordType != null) {
        return TyNativeList.WRAP(recordType).withPosition(this);
      }
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    expression.writeJava(sb, environment);
    sb.append(".iterate(").append(intermediateExpression ? "false" : "true").append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
