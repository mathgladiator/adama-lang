/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.DetailNeverPublic;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.HashMap;
import java.util.function.Consumer;

public class TyNativeGlobalObject extends TyType implements //
    DetailNeverPublic, //
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
  public void format(Formatter formatter) {
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
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
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
