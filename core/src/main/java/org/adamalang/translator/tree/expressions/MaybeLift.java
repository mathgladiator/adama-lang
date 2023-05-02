/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;

import java.util.function.Consumer;

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
    environment.mustBeComputeContext(this);
    if (type != null) {
      return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, maybeToken, type);
    } else {
      final var valueType = value.typing(environment, null);
      if (valueType != null) {
        return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, maybeToken, new TokenizedItem<>(valueType));
      }
      return null;
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

  @Override
  public void free(FreeEnvironment environment) {
    if (value != null) {
      value.free(environment);
    }
  }
}
