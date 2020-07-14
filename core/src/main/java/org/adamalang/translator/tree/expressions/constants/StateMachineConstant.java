/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.constants;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeStateMachineRef;

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
    if (token.text.length() > 1) { // we treat # as as special case
      if (environment.rules.FindStateMachineStep(value, this, false) != null) { return new TyNativeStateMachineRef(token).makeCopyWithNewPosition(this); }
      return null;
    } else {
      return new TyNativeStateMachineRef(token).makeCopyWithNewPosition(this);
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    environment.mustBeComputeContext(this);
    sb.append("\"").append(value).append("\"");
  }
}
