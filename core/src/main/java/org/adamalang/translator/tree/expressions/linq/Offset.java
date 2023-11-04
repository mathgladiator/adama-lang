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

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;

import java.util.function.Consumer;

/** skip the first elements of a list */
public class Offset extends LinqExpression {
  public final Token offsetToken;
  public final Expression offset;

  public Offset(final Expression sql, final Token offsetToken, final Expression offset) {
    super(sql);
    ingest(sql);
    this.offsetToken = offsetToken;
    this.offset = offset;
    ingest(offset);
  }


  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(offsetToken);
    offset.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
    offset.format(formatter);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    final var typeSql = sql.typing(computeEnv, null);
    environment.rules.IsNativeListOfStructure(typeSql, false);
    environment.rules.IsInteger(offset.typing(computeEnv, null), false);
    return typeSql;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    sql.writeJava(sb, environment);
    sb.append(".skip(").append(intermediateExpression ? "false, " : "true, ");
    offset.writeJava(sb, computeEnv);
    sb.append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    offset.free(environment);
  }
}
