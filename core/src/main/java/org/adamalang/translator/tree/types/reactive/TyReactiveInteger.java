/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.IntegerConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.traits.IsOrderable;

/** Represents the integral with 32 bits of storage; this uses the 'RxInt32' reactive java type */
public class TyReactiveInteger extends TySimpleReactive implements //
    IsOrderable //
{
  public TyReactiveInteger(final Token token) {
    super(token, "RxInt32");
  }

  @Override
  public String getAdamaType() {
    return "r<int>";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveInteger(token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("int");
    writer.endObject();
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition position) {
    return new IntegerConstant(Token.WRAP("0"), 0).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, token);
  }
}
