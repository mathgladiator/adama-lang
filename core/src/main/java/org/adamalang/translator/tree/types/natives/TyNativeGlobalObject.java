/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.natives;

import java.util.HashMap;
import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyNativeGlobalObject extends TyType implements DetailTypeHasMethods {
  public final HashMap<String, TyNativeFunctional> functions;
  public final String globalName;
  public final String importPackage;
  public final boolean availableForStatic;

  public TyNativeGlobalObject(final String globalName, final String importPackage, boolean availableForStatic) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.globalName = globalName;
    this.importPackage = importPackage;
    this.availableForStatic = availableForStatic;
    functions = new HashMap<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAdamaType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if (environment.state.isStatic() && !availableForStatic) {
      return null;
    }
    final var found = functions.get(name);
    if (found != null) { return found; }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeGlobalObject(globalName, null, availableForStatic).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("global");
    writer.endObject();
  }
}
