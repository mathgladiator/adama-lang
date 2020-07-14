/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.constants;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

/** The only built-in client constant */
public class NoOneClientConstant extends Expression {
  public final Token token;

  public NoOneClientConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return new TyNativeClient(token).makeCopyWithNewPosition(this);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    environment.mustBeComputeContext(this);
    sb.append("NtClient.NO_ONE");
  }
}
