/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions;

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;

public class MaybeLift extends Expression {
  public final Token closeParen;
  public final Token maybeToken;
  public final Token openParen;
  public final TokenizedItem<TyType> type;
  public final Expression value;

  public MaybeLift(final Token maybeToken, final TokenizedItem<TyType> type, final Token openParen, final Expression value, final Token closeParen) {
    this.maybeToken = maybeToken;
    this.type = type;
    this.openParen = openParen;
    this.value = value;
    this.closeParen = closeParen;
    ingest(maybeToken);
    if (type != null) {
      ingest(type.item);
    } else {
      ingest(closeParen);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(maybeToken);
    if (type != null) {
      type.emitBefore(yielder);
      type.item.emit(yielder);
      type.emitAfter(yielder);
    } else {
      yielder.accept(openParen);
      value.emit(yielder);
      yielder.accept(closeParen);
    }
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    if (type != null) {
      return new TyNativeMaybe(maybeToken, type);
    } else {
      final var valueType = value.typing(environment, null);
      return new TyNativeMaybe(maybeToken, new TokenizedItem<>(valueType));
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("new ");
    sb.append(environment.rules.Resolve(cachedType, true).getJavaBoxType(environment));
    sb.append("(");
    if (value != null) {
      value.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    }
    sb.append(")");
  }
}
