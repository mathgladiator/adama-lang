/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.linq;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeList;

/** begin a linq query to convert a table into a list which may be filtered,
 * ordered, limited */
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
      if (recordType != null) { return TyNativeList.WRAP(recordType).withPosition(this); }
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    expression.writeJava(sb, environment);
    sb.append(".iterate(").append(intermediateExpression ? "false" : "true").append(")");
  }
}
