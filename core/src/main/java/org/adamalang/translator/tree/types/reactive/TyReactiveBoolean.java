/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.BooleanConstant;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.traits.IsOrderable;

/** Represents the reactive type for a boolean value (true/false); this uses the
 * reactive 'TBoolean' java type */
public class TyReactiveBoolean extends TySimpleReactive implements IsOrderable //
{
  public TyReactiveBoolean(final Token token) {
    super(token, "RxBoolean");
  }

  @Override
  public String getAdamaType() {
    return "bool";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new BooleanConstant(Token.WRAP("false"), false).withPosition(forWhatExpression);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveBoolean(token).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, token);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_value");
    writer.writeObjectFieldIntro("type");
    writer.writeString("bool");
    writer.endObject();
  }
}
