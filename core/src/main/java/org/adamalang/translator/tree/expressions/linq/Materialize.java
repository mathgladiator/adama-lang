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
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

import java.util.function.Consumer;

public class Materialize extends LinqExpression {
  private final Token token;
  private boolean actuallyMaterialize;
  private String subType;
  private int indexCount;

  public Materialize(final Expression sql, final Token token) {
    super(sql);
    this.token = token;
    ingest(token);
    this.actuallyMaterialize = false;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(token);
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    TyType baseType = sql.typing(environment, suggestion);
    if (environment.rules.IsNativeListOfStructure(baseType, false)) {
      TyType elementTypeRaw = environment.rules.Resolve(((DetailContainsAnEmbeddedType) baseType).getEmbeddedType(environment), false);
      IsStructure elementType = (IsStructure) elementTypeRaw;
      subType = elementTypeRaw.getJavaBoxType(environment);
      this.indexCount = elementType.storage().indexSet.size();
      this.actuallyMaterialize = indexCount > 0;
    }
    return baseType;
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    if (actuallyMaterialize) {
      sb.append("new MaterializedNtList<").append(subType).append(">(");
      sql.writeJava(sb, environment);
      sb.append(",").append(indexCount).append(")");
    } else {
      sql.writeJava(sb, environment);
    }
  }
}
