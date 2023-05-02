/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.HashMap;
import java.util.function.Consumer;

public class TyNativeGlobalObject extends TyType implements //
    DetailTypeHasMethods {
  public final HashMap<String, TyNativeFunctional> functions;
  public final String globalName;
  public final String importPackage;
  public final boolean availableForStatic;
  private TyNativeGlobalObject parentOverride;

  public TyNativeGlobalObject(final String globalName, final String importPackage, boolean availableForStatic) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.globalName = globalName;
    this.importPackage = importPackage;
    this.availableForStatic = availableForStatic;
    this.parentOverride = null;
    functions = new HashMap<>();
  }

  public void setParentOverride(TyNativeGlobalObject parentOverride) {
    this.parentOverride = parentOverride;
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
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
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
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
    writeAnnotations(writer);
    writer.endObject();
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if (parentOverride != null && !functions.containsKey(name)) {
      TyNativeFunctional result = parentOverride.lookupMethod(name, environment);
      if (result != null) {
        return result;
      }
    }
    if (environment.state.isStatic() && !availableForStatic) {
      return null;
    }
    return functions.get(name);
  }
}
