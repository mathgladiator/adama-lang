/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.DoubleConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

/** represents a double precision floating point number. For instance, 3.14 is a
 * floating point number. This uses the native 'double' java type. */
public class TyNativeDouble extends TySimpleNative implements //
    IsNativeValue, DetailHasBridge, //
    DetailEqualityTestingRequiresWrapping, //
    AssignmentViaNative //
{
  public final Token token;

  public TyNativeDouble(final Token token) {
    super("double", "Double");
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "double";
  }

  @Override
  public String getBridge(final Environment environment) {
    return "NativeBridge.DOUBLE_NATIVE_SUPPORT";
  }

  @Override
  public String getEqualityTestingBinaryPattern() {
    return "LibMath.near(%s, %s)";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new DoubleConstant(Token.WRAP("0.0"), 0.0).withPosition(forWhatExpression);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeDouble(token).withPosition(position);
  }
}