/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        environment.document.createError(this, "Field '" + field + "' had a type we didn't recognize in internal type: " + clazz.getSimpleName());
        return null;
      }
    } catch (Exception ex) {
      environment.document.createError(this, "Field '" + field + "' was not found in internal type: " + clazz.getSimpleName());
      return null;
    }
  }
}
