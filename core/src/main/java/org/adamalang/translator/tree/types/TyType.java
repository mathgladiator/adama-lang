/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

public abstract class TyType extends DocumentPosition {
  public abstract void emit(Consumer<Token> yielder);
  public abstract String getAdamaType();
  public abstract String getJavaBoxType(Environment environment);
  public abstract String getJavaConcreteType(Environment environment);
  public abstract TyType makeCopyWithNewPosition(DocumentPosition position);
  public abstract void typing(Environment environment);

  public TyType withPosition(final DocumentPosition position) {
    reset();
    ingest(position);
    return this;
  }
}
