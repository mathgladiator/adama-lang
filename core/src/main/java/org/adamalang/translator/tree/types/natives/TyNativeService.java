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
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.DefineService;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeService extends TyType implements //
    DetailTypeHasMethods {
  public final DefineService service;

  public TyNativeService(final DefineService service) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.service = service;
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAdamaType() {
    return "service";
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
    return new TyNativeService(service).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("service");
    writer.writeObjectFieldIntro("service");
    writer.writeString(service.name.text);
    writeAnnotations(writer);
    writer.endObject();
  }

  private TyType lookupType(String name, Environment environment) {
    if ("dynamic".equals(name)) {
      return new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, null);
    }
    return environment.rules.FindMessageStructure(name, this, true).withPosition(service);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    DefineService.ServiceMethod method = service.methodsMap.get(name);
    if (method != null) {
      ArrayList<TyType> argTypes = new ArrayList<>();
      {
        if (method.requiresSecureCaller()) {
          argTypes.add(new TyNativeSecurePrincipal(TypeBehavior.ReadWriteNative, null, null, null, null, null).withPosition(service));
        } else {
          argTypes.add(new TyNativePrincipal(TypeBehavior.ReadWriteNative, null, null).withPosition(service));
        }
        argTypes.add(lookupType(method.inputTypeName.text, environment));
      }
      TyType outputType = lookupType(method.outputTypeName.text, environment);
      if (method.outputArrayExt != null) {
        outputType = new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, outputType, method.outputArrayExt);
      }
      outputType = outputType.withPosition(service);
      outputType = new TyNativeResult(TypeBehavior.ReadOnlyNativeValue, null, method.methodToken, new TokenizedItem<>(outputType)).withPosition(this);
      return new TyNativeFunctional(name, FunctionOverloadInstance.WRAP(new FunctionOverloadInstance(name, outputType, argTypes, FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.RemoteCall);
    }
    return null;
  }
}
