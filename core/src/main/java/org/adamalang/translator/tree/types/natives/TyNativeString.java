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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.StringConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailComparisonTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailSpecialMultiplyOp;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

/** The type representing a utf-8 encoded string. This uses the native 'String'
 * java type. */
public class TyNativeString extends TySimpleNative implements IsNativeValue, DetailHasDeltaType, //
    CanBeMapDomain, //
    DetailTypeHasMethods, //
    DetailSpecialMultiplyOp, //
    DetailEqualityTestingRequiresWrapping, //
    DetailComparisonTestingRequiresWrapping, //
    AssignmentViaNative //
{
  public final Token readonlyToken;
  public final Token token;

  public TyNativeString(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "String", "String");
    this.readonlyToken = readonlyToken;
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "string";
  }

  @Override
  public String getComparisonTestingBinaryPattern() {
    return "LibString.compare(%s, %s)";
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DString";
  }

  @Override
  public String getEqualityTestingBinaryPattern() {
    return "LibString.equality(%s, %s)";
  }

  @Override
  public String getSpecialMultiplyOpPatternForBinary() {
    return "LibString.multiply(%s, %s)";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new StringConstant(Token.WRAP("\"\"")).withPosition(forWhatExpression);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("length".equals(name)) {
      return new TyNativeFunctional("length", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, token).withPosition(this), new ArrayList<>(), true)),
          FunctionStyleJava.ExpressionThenArgs);
    }
    return environment.state.globals.findExtension(this, name);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeString(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writer.writeObjectFieldIntro("type");
    writer.writeString("string");
    writer.endObject();
  }

  @Override
  public String getRxStringCodexName() {
    return "RxMap.StringCodec";
  }

}
