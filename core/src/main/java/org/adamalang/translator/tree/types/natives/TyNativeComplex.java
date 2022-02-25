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
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.ComplexConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * represents a double precision floating point number. For instance, 3.14 is a floating point
 * number. This uses the native 'double' java type.
 */
public class TyNativeComplex extends TySimpleNative implements //
    IsNativeValue, //
    DetailHasDeltaType, //
    DetailTypeHasMethods, //
    DetailEqualityTestingRequiresWrapping, //
    AssignmentViaNative //
{
  public final Token readonlyToken;
  public final Token token;

  public TyNativeComplex(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "NtComplex", "NtComplex");
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
    return "complex";
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeComplex(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writer.writeObjectFieldIntro("type");
    writer.writeString("complex");
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DComplex";
  }

  @Override
  public String getEqualityTestingBinaryPattern() {
    return "LibMath.near(%s, %s)";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new ComplexConstant(0.0, 0.0, Token.WRAP("0.0")).withPosition(forWhatExpression);
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    if ("re".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("real", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("real", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(this), new ArrayList<>(), false)), FunctionStyleJava.None);
    } else if ("im".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("imaginary", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("imaginary", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(this), new ArrayList<>(), false)), FunctionStyleJava.None);
    }
    return environment.state.globals.findExtension(this, name);
  }
}
