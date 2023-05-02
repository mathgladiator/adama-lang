/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.natives.TyNativePair;

import java.util.function.Consumer;

public class PairCons extends Expression {

  public final Token pairIntro;
  public final Expression key;
  public final Token arrow;
  public final Expression value;

  public PairCons(Token pairIntro, Expression key, Token arrow, Expression value) {
    this.pairIntro = pairIntro;
    this.key = key;
    this.arrow = arrow;
    this.value = value;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(pairIntro);
    key.emit(yielder);
    yielder.accept(arrow);
    value.emit(yielder);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    TyType keyType = null;
    TyType valueType = null;
    if (suggestion instanceof TyNativePair) {
      keyType = ((TyNativePair) suggestion).domainType;
      valueType = ((TyNativePair) suggestion).rangeType;
    }
    keyType = key.typing(environment, keyType);
    valueType = value.typing(environment, valueType);
    return new TyNativePair(TypeBehavior.ReadOnlyNativeValue, null, pairIntro, null, keyType, null, valueType, arrow);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    sb.append("new NtPair<>(");
    key.writeJava(sb, environment);
    sb.append(",");
    value.writeJava(sb, environment);
    sb.append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    key.free(environment);
    value.free(environment);
  }
}
