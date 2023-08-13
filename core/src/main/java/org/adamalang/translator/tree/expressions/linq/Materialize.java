/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
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
