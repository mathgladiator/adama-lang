/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDate;
import org.adamalang.translator.tree.types.natives.TyNativeDateTime;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

/** a date and a time with the time zone in the typical gregorian calendar as a constant within source */
public class DateTimeConstant extends Expression {
  public final Token[] tokens;
  public final ZonedDateTime dateTime;

  public DateTimeConstant(ZonedDateTime dateTime, Token... tokens) {
    this.dateTime = dateTime;
    this.tokens = tokens;
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
    return new TyNativeDateTime(TypeBehavior.ReadOnlyNativeValue, null, tokens[0]).withPosition(this);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("new NtDateTime(ZonedDateTime.parse(\"").append(dateTime.toString()).append("\"))");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
