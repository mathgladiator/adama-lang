/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.DoubleConstant;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;
import org.adamalang.translator.tree.types.traits.IsOrderable;

/** represents a double precision floating point number. For instance, 3.14 is a
 * floating point number. This uses the reactive 'RxDouble' java type. */
public class TyReactiveDouble extends TySimpleReactive implements IsOrderable //
{
  public TyReactiveDouble(final Token token) {
    super(token, "RxDouble");
  }

  @Override
  public String getAdamaType() {
    return "double";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new DoubleConstant(Token.WRAP("0.0"), 0.0).withPosition(forWhatExpression);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveDouble(token).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, token);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_value");
    writer.writeObjectFieldIntro("type");
    writer.writeString("double");
    writer.endObject();
  }
}
