/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class TyInternalReadonlyClass extends TyType {
  private final Class<?> clazz;

  public TyInternalReadonlyClass(Class<?> clazz) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.clazz = clazz;
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    throw new UnsupportedOperationException("internal types can't be emitted");
  }

  @Override
  public String getAdamaType() {
    return "internal<" + clazz.getSimpleName() + ">";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return clazz.getName();
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return clazz.getName();
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyInternalReadonlyClass(this.clazz).withPosition(position);
  }

  @Override
  public void typing(Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    throw new UnsupportedOperationException("internal types can't be reflected");
  }

  public TyType getLookupType(Environment environment, String field) {
    try {
      Field fType = clazz.getField(field);
      if (fType.getType() == String.class) {
        return new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string"));
      } else if (fType.getType() == NtPrincipal.class) {
        return new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("client"));
      } else {
        environment.document.createError(this, "Field '" + field + "' had a type we didn't recognize in internal type: " + clazz.getSimpleName(), "InternalTypes");
        return null;
      }
    } catch (Exception ex) {
      environment.document.createError(this, "Field '" + field + "' was not found in internal type: " + clazz.getSimpleName(), "InternalTypes");
      return null;
    }
  }
}
