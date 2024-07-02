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
import org.adamalang.translator.codegen.CodeGenEnums;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineDispatcher;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.EnumConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.traits.*;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailSpecialReactiveRefResolve;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;
import org.adamalang.translator.tree.types.traits.details.DetailTypeProducesRootLevelCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TyNativeEnum extends TySimpleNative implements //
    IsNativeValue, //
    IsOrderable, //
    DetailHasDeltaType, //
    CanBeMapDomain, //
    DetailSpecialReactiveRefResolve, //
    DetailTypeProducesRootLevelCode, //
    DetailTypeHasMethods, //
    DetailCanExtractForUnique, //
    IsEnum, //
    AssignmentViaNative //
{
  public final Token endBrace;
  public final Token enumToken;
  public final String name;
  public final Token nameToken;
  public final Token openBrace;
  public final EnumStorage storage;

  public TyNativeEnum(final TypeBehavior behavior, final Token enumToken, final Token nameToken, final Token openBrace, final EnumStorage storage, final Token endBrace) {
    super(behavior, "int", "Integer", 4);
    this.enumToken = enumToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.openBrace = openBrace;
    this.storage = storage;
    this.endBrace = endBrace;
    ingest(enumToken);
    ingest(nameToken);
    ingest(endBrace);
    storage.ingest(this);
  }

  @Override
  public void compile(final StringBuilderWithTabs sb, final Environment environment) {
    CodeGenEnums.writeEnumArray(sb, name, "ALL_VALUES", "", storage);
    CodeGenEnums.writeEnumNextPrevString(sb, name, storage);
    CodeGenEnums.writeEnumFixer(sb, name, storage);
    for (final Map.Entry<String, HashMap<String, ArrayList<DefineDispatcher>>> dispatchers : storage.dispatchersByNameThenSignature.entrySet()) {
      CodeGenEnums.writeDispatchers(sb, storage, dispatchers.getValue(), dispatchers.getKey(), environment);
    }
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    yielder.accept(enumToken);
    yielder.accept(nameToken);
    yielder.accept(openBrace);
    storage.emit(yielder);
    yielder.accept(endBrace);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(enumToken);
    formatter.endLine(openBrace);
    formatter.tabUp();
    storage.format(formatter);
    formatter.tabDown();
    formatter.endLine(endBrace);
  }

  @Override
  public String getAdamaType() {
    return name;
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeEnum(newBehavior, enumToken, nameToken, openBrace, storage, endBrace).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("enum");
    writer.writeObjectFieldIntro("enum");
    writer.writeString(name);
    writer.writeObjectFieldIntro("options");
    storage.writeTypeReflectionJson(writer);
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DInt32";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new EnumConstant(Token.WRAP(name), Token.WRAP("::"), Token.WRAP(storage.getDefaultLabel())).withPosition(forWhatExpression);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("to_int".equals(name)) {
      return new TyNativeFunctional("to_int", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("Utility.identity", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, enumToken).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("next".equals(name)) {
      return new TyNativeFunctional("next", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__EnumCycleNext_" + this.name, this, new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("prev".equals(name)) {
      return new TyNativeFunctional("prev", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__EnumCyclePrev_" + this.name, this, new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("to_string".equals(name)) {
      return new TyNativeFunctional("to_string", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__EnumString_" + this.name, new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, enumToken), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    return storage.computeDispatcherType(name);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public EnumStorage storage() {
    return storage;
  }

  @Override
  public TyType typeAfterReactiveRefResolve(final Environment environment) {
    return new TyReactiveEnum(false, nameToken, storage).withPosition(this);
  }

  @Override
  public String getRxStringCodexName() {
    return "RxMap.IntegerCodec";
  }
}
