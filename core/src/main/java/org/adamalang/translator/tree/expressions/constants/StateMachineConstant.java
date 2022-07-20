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
import org.adamalang.translator.tree.types.natives.TyNativeStateMachineRef;

import java.util.function.Consumer;

/** a reference to a state within the state machine (#label) */
public class StateMachineConstant extends Expression {
  public final Token token;
  public final String value;

  /** @param token the token containing the value along with prior/after */
  public StateMachineConstant(final Token token) {
    this.token = token;
    value = token.text.substring(1);
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.interns.add("\"" + value + "\"");
    environment.mustBeComputeContext(this);
    if (token.text.length() > 1) { // we treat # as as special case
      if (environment.rules.FindStateMachineStep(value, this, false) != null) {
        return new TyNativeStateMachineRef(TypeBehavior.ReadOnlyNativeValue, null, token).withPosition(this);
      }
      return null;
    } else {
      return new TyNativeStateMachineRef(TypeBehavior.ReadOnlyNativeValue, null, token).withPosition(this);
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("\"").append(value).append("\"");
  }
}
