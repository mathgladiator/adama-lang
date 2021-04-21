/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types;

import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

public abstract class TyType extends DocumentPosition {
  public final TypeBehavior behavior;

  public TyType(final TypeBehavior behavior) {
    this.behavior = behavior;
  }

  public abstract void emit(Consumer<Token> yielder);
  public abstract String getAdamaType();
  public abstract String getJavaBoxType(Environment environment);
  public abstract String getJavaConcreteType(Environment environment);
  public abstract TyType makeCopyWithNewPosition(DocumentPosition position, TypeBehavior newBehavior);
  public abstract void typing(Environment environment);
  public abstract void writeTypeReflectionJson(JsonStreamWriter writer);

  public TyType withPosition(final DocumentPosition position) {
    reset();
    ingest(position);
    return this;
  }
}
