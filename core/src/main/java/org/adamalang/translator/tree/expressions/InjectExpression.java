/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;

public abstract class InjectExpression extends Expression {
  public final TyType type;

  public InjectExpression(final TyType type) {
    this.type = type;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return type;
  }
}
