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

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
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

  public TyType getLookupType(Environment environment, String field) {
    try {
      Field fType = clazz.getField(field);
      if (fType.getType() == String.class) {
        return new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string"));
      } else if (fType.getType() == NtClient.class) {
        return new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("client"));
      } else {
        environment.document.createError(this, "Field '" + field + "' had a type we didn't recognize in internal type: " + clazz.getSimpleName(), "InternalTypes");
        return null;
      }
    } catch (Exception ex) {
      environment.document.createError(this, "Field '" + field + "' was not found in internal type: " + clazz.getSimpleName(), "InternalTypes");
      return null;
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    throw new UnsupportedOperationException("internal types can't be reflected");
  }
}
