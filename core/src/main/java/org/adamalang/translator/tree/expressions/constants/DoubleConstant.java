/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.constants;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;

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
    return new TyNativeDouble(token).makeCopyWithNewPosition(this);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    environment.mustBeComputeContext(this);
    sb.append(value);
  }
}
