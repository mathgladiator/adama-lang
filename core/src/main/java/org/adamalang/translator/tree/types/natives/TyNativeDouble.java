/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.DoubleConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;

/** represents a double precision floating point number. For instance, 3.14 is a
 * floating point number. This uses the native 'double' java type. */
public class TyNativeDouble extends TySimpleNative implements //
    IsNativeValue, //
    DetailHasDeltaType, //
    DetailEqualityTestingRequiresWrapping, //
    AssignmentViaNative //
{
  public final Token readonlyToken;
  public final Token token;

  public TyNativeDouble(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "double", "Double");
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
    return "double";
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DDouble";
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
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeDouble(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writer.writeObjectFieldIntro("type");
    writer.writeString("double");
    writer.endObject();
  }
}
