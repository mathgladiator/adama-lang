/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.IntegerConstant;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.traits.IsOrderable;

/** Represents the integral with 32 bits of storage; this uses the 'RxInt32'
 * reactive java type */
public class TyReactiveInteger extends TySimpleReactive implements IsOrderable //
{
  public TyReactiveInteger(final Token token) {
    super(token, "RxInt32");
  }

  @Override
  public String getAdamaType() {
    return "int";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition position) {
    return new IntegerConstant(Token.WRAP("0"), 0).withPosition(position);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyReactiveInteger(token).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return new TyNativeInteger(token);
  }
}