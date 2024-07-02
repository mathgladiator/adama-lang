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
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.DefineFunction;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.DetailNeedsSettle;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyReactiveProjectionMap extends TyType implements //
    DetailTypeHasMethods, //
    DetailNeedsSettle {
  private final Token projectionToken;
  private final Token open;
  private final Token tableVar;
  private final Token dot;
  private final Token field;
  private final Token close;
  private final boolean readonly;
  private final boolean policy;
  private TyType rangeType;

  public TyReactiveProjectionMap(Token projectionToken, Token open, Token tableVar, Token dot, Token field, Token close, boolean readonly, boolean policy) {
    super(TypeBehavior.ReadOnlyWithGet);
    this.projectionToken = projectionToken;
    this.open = open;
    this.tableVar = tableVar;
    this.dot = dot;
    this.field = field;
    this.close = close;
    this.readonly = readonly;
    this.policy = policy;
    ingest(projectionToken);
    ingest(close);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    yielder.accept(projectionToken);
    yielder.accept(open);
    yielder.accept(tableVar);
    yielder.accept(dot);
    yielder.accept(field);
    yielder.accept(close);
  }

  @Override
  public String getAdamaType() {
    return "projection<" + tableVar.text + "." + field.text + ">";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return getJavaConcreteType(environment);
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    // return "RxProjectionMap<" + environment.document.pureFunctions.get(funcName.text).returnType.getJavaBoxType(environment) + ">";
    return null;
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyReactiveProjectionMap(projectionToken, open, tableVar, dot, field, close, readonly, policy).withPosition(position);
  }

  @Override
  public void typing(Environment environment) {
    if (readonly) {
      environment.document.createError(this, "projection maps are readonly by default and the annotation is not welcome.");
    }
    if (policy) {
      environment.document.createError(this, "projection maps must be private.");
    }
    TyType tableType = environment.rules.Resolve(environment.lookup(tableVar.text, true, this, false), false);
    if (!(tableType instanceof TyReactiveTable)) {
      environment.document.createError(this, tableVar.text + " must be a reactive table");
      return;
    }
    TyReactiveRecord recordType = (TyReactiveRecord) environment.rules.Resolve(((TyReactiveTable) tableType).getEmbeddedType(environment), false);
    FieldDefinition fd = recordType.storage.fields.get(field.text);
    if (fd == null) {
      environment.document.createError(this, "'" + field.text + "' was not a field within table '" + tableVar.text + "'");
      return;
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_projection");
    writeAnnotations(writer);
    writer.endObject();
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    return null;
  }
}
