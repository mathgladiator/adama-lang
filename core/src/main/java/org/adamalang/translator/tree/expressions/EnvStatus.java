/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.expressions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;

/** query the environment for status about the queue etc... */
public class EnvStatus extends Expression {
  public final Token token;
  public final EnvLookupName variable;

  public EnvStatus(final Token token, final EnvLookupName variable) {
    this.token = token;
    this.variable = variable;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, token).withPosition(this);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (variable == EnvLookupName.Blocked) {
      sb.append("__blocked.get()");
      return;
    }
    sb.append("(!__state.has())");
  }
}
