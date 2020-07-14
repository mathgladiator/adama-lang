/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.StringConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailComparisonTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailSpecialMultiplyOp;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

/** The type representing a utf-8 encoded string. This uses the native 'String'
 * java type. */
public class TyNativeString extends TySimpleNative implements IsNativeValue, DetailHasBridge, //
    CanBeMapDomain, //
    DetailTypeHasMethods, //
    DetailSpecialMultiplyOp, //
    DetailEqualityTestingRequiresWrapping, //
    DetailComparisonTestingRequiresWrapping, //
    AssignmentViaNative //
{
  public final Token token;

  public TyNativeString(final Token token) {
    super("String", "String");
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "string";
  }

  @Override
  public String getBridge(final Environment environment) {
    return "NativeBridge.STRING_NATIVE_SUPPORT";
  }

  @Override
  public String getComparisonTestingBinaryPattern() {
    return "LibString.compare(%s, %s)";
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
      return new TyNativeFunctional("length", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(token).withPosition(this), new ArrayList<>(), true)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("reverse".equals(name)) {
      return new TyNativeFunctional("reverse", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("LibString.reverse", new TyNativeString(token).withPosition(this), new ArrayList<>(), true)),
          FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeString(token).withPosition(position);
  }
}
