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

/** limit the sql expression by a number and offset */
public class Limit extends LinqExpression {
  public final Expression limit;
  public final Token limitToken;

  public Limit(final Expression sql, final Token limitToken, final Expression limit) {
    super(sql);
    this.limitToken = limitToken;
    this.limit = limit;
    ingest(sql);
    ingest(limit);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(limitToken);
    limit.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
    limit.format(formatter);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    final var typeSql = sql.typing(computeEnv, null);
    environment.rules.IsNativeListOfStructure(typeSql, false);
    final var limitType = limit.typing(computeEnv, null);
    environment.rules.IsInteger(limitType, false);
    return typeSql;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    sql.writeJava(sb, environment);
    sb.append(".limit(").append(intermediateExpression ? "false, " : "true, ");
    limit.writeJava(sb, computeEnv);
    sb.append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    limit.free(environment);
  }
}
