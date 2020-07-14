/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.IntegerConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

/** Represents the integral with 32 bits of storage; this uses the 'int' java
 * type */
public class TyNativeInteger extends TySimpleNative implements IsNativeValue, //
    CanBeMapDomain, //
    DetailHasBridge, //
    IsOrderable, //
    AssignmentViaNative //
{
  public final Token token;

  public TyNativeInteger(final Token token) {
    super("int", "Integer");
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "int";
  }

  @Override
  public String getBridge(final Environment environment) {
    return "NativeBridge.INTEGER_NATIVE_SUPPORT";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition position) {
    return new IntegerConstant(Token.WRAP("0"), 0).withPosition(position);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeInteger(token).withPosition(position);
  }
}
