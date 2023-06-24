/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;

import java.util.function.Consumer;

/** double-wide floating point precision numbers (3.14) */
public class DoubleConstant extends Expression {
  public final Token token;
  public final double value;

  public DoubleConstant(final Token token, final double value) {
    this.token = token;
    this.value = value;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    return new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, token).makeCopyWithNewPosition(this, TypeBehavior.ReadOnlyNativeValue);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append(value);
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
