/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
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
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailSpecialReactiveRefResolve;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;
import org.adamalang.translator.tree.types.traits.details.DetailTypeProducesRootLevelCode;

public class TyNativeEnum extends TySimpleNative implements IsNativeValue, DetailHasBridge, //
    CanBeMapDomain, //
    DetailSpecialReactiveRefResolve, //
    DetailTypeProducesRootLevelCode, //
    DetailTypeHasMethods, //
    IsEnum, //
    AssignmentViaNative //
{
  private final Token endBrace;
  public final Token enumToken;
  public final String name;
  public final Token nameToken;
  private final Token openBrace;
  public final EnumStorage storage;

  public TyNativeEnum(final Token enumToken, final Token nameToken, final Token openBrace, final EnumStorage storage, final Token endBrace) {
    super("int", "Integer");
    this.enumToken = enumToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.openBrace = openBrace;
    this.storage = storage;
    this.endBrace = endBrace;
    ingest(enumToken);
    ingest(nameToken);
    ingest(storage);
  }

  @Override
  public void compile(final StringBuilderWithTabs sb, final Environment environment) {
    CodeGenEnums.writeEnumArray(sb, name, "ALL_VALUES", "", storage);
    for (final Map.Entry<String, HashMap<String, ArrayList<DefineDispatcher>>> dispatchers : storage.dispatchersByNameThenSignature.entrySet()) {
      CodeGenEnums.writeDispatchers(sb, storage, dispatchers.getValue(), dispatchers.getKey(), environment);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
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
  public String getBridge(final Environment environment) {
    return "NativeBridge.INTEGER_NATIVE_SUPPORT";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new EnumConstant(Token.WRAP(name), Token.WRAP("::"), Token.WRAP(storage.getDefaultLabel())).withPosition(forWhatExpression);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("to_int".equals(name)) {
      return new TyNativeFunctional("to_int", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("Utility.identity", new TyNativeInteger(enumToken).makeCopyWithNewPosition(this), new ArrayList<>(), true)),
          FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    return storage.computeDispatcherType(name);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeEnum(enumToken, nameToken, openBrace, storage, endBrace).withPosition(position);
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
    return new TyReactiveEnum(nameToken, storage);
  }
}
