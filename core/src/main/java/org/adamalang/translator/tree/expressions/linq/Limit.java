/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.expressions.linq;

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;

/** limit the sql expression by a number and offset */
public class Limit extends LinqExpression {
  public final Expression limit;
  public final Token limitToken;
  public final Expression offset;
  public final Token offsetToken;

  public Limit(final Expression sql, final Token limitToken, final Expression limit, final Token offsetToken, final Expression offset) {
    super(sql);
    this.limitToken = limitToken;
    this.limit = limit;
    ingest(sql);
    ingest(limit);
    this.offsetToken = offsetToken;
    this.offset = offset;
    if (offsetToken != null) {
      ingest(offset);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(limitToken);
    limit.emit(yielder);
    if (offsetToken != null) {
      yielder.accept(offsetToken);
      offset.emit(yielder);
    }
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    final var typeSql = sql.typing(computeEnv, null);
    environment.rules.IsNativeListOfStructure(typeSql, false);
    final var limitType = limit.typing(computeEnv, null);
    environment.rules.IsInteger(limitType, false);
    if (offset != null) {
      final var offsetType = offset.typing(computeEnv, null);
      environment.rules.IsInteger(offsetType, false);
    }
    return typeSql;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    sql.writeJava(sb, environment);
    if (offset != null) {
      sb.append(".skipAndLimit(").append(intermediateExpression ? "false, " : "true, ");
      offset.writeJava(sb, computeEnv);
      sb.append(", ");
      limit.writeJava(sb, computeEnv);
      sb.append(")");
    } else {
      sb.append(".skipAndLimit(").append(intermediateExpression ? "false, 0, " : "true, 0, ");
      limit.writeJava(sb, computeEnv);
      sb.append(")");
    }
  }
}
