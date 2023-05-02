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
import org.adamalang.translator.codegen.CodeGenEnums;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineDispatcher;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.EnumConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsOrderable;
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
    super(behavior, "int", "Integer");
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
    CodeGenEnums.writeEnumNextPrev(sb, name, storage);
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
  public String getAdamaType() {
    return name;
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeEnum(newBehavior, enumToken, nameToken, openBrace, storage, endBrace).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
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
      return new TyNativeFunctional("to_int", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("Utility.identity", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, enumToken).withPosition(this), new ArrayList<>(), true, false, false)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("next".equals(name)) {
      return new TyNativeFunctional("next", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__EnumCycleNext_" + this.name, this, new ArrayList<>(), true, false, false)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("prev".equals(name)) {
      return new TyNativeFunctional("prev", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__EnumCyclePrev_" + this.name, this, new ArrayList<>(), true, false, false)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
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
    return new TyReactiveEnum(nameToken, storage).withPosition(this);
  }

  @Override
  public String getRxStringCodexName() {
    return "RxMap.IntegerCodec";
  }
}
