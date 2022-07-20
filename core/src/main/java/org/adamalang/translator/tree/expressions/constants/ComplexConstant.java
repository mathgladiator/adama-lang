/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeComplex;

import java.util.function.Consumer;

public class ComplexConstant extends Expression {
  public final Token[] tokens;
  public final double real;
  public final double imaginary;

  public ComplexConstant(final double real, final double imaginary, final Token... tokens) {
    this.tokens = tokens;
    this.real = real;
    this.imaginary = imaginary;
    for (Token token : tokens) {
      ingest(token);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    for (Token token : tokens) {
      yielder.accept(token);
    }
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    return new TyNativeComplex(TypeBehavior.ReadOnlyNativeValue, null, tokens[0]).withPosition(this);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("new NtComplex(").append(real).append(", ").append(imaginary).append(")");
  }
}
