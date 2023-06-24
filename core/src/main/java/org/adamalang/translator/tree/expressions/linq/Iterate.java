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
