/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.LongConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;

/** Represents the integral with 64 bits of storage; this uses the 'long' java
 * type */
public class TyNativeLong extends TySimpleNative implements IsNativeValue, //
    CanBeMapDomain, //
    DetailHasDeltaType, //
    IsOrderable, //
    AssignmentViaNative //
{
  public final Token readonlyToken;
  public final Token token;

  public TyNativeLong(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "long", "Long");
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
    return "long";
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DInt64";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition position) {
    return new LongConstant(Token.WRAP("0L"), 0).withPosition(position);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeLong(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writer.writeObjectFieldIntro("type");
    writer.writeString("long");
    writer.endObject();
  }
}
