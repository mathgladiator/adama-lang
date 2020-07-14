/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.StringConstant;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.traits.IsOrderable;

/** The type representing a utf-8 encoded string. This uses the reactive
 * 'RxString' java type. */
public class TyReactiveString extends TySimpleReactive implements IsOrderable //
{
  public TyReactiveString(final Token token) {
    super(token, "RxString");
  }

  @Override
  public String getAdamaType() {
    return "string";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new StringConstant(Token.WRAP("\"\"")).withPosition(forWhatExpression);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyReactiveString(token).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return new TyNativeString(token);
  }
}
