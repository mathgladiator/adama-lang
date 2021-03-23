/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

public class TyNativeFunctional extends TyType {
  public final String name;
  public final ArrayList<FunctionOverloadInstance> overloads;
  public final FunctionStyleJava style;

  public TyNativeFunctional(final String name, final ArrayList<FunctionOverloadInstance> overloads, final FunctionStyleJava style) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.name = name;
    this.overloads = overloads;
    this.style = style;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException();
  }

  /** find the right instance basedd on the given types */
  public FunctionOverloadInstance find(final DocumentPosition position, final ArrayList<TyType> argTypes, final Environment environment) {
    var result = overloads.get(0);
    var score = (argTypes.size() + 1) * (argTypes.size() + 1);
    for (final FunctionOverloadInstance candidate : overloads) {
      final var testScore = candidate.score(environment, argTypes);
      if (testScore < score) {
        score = testScore;
        result = candidate;
      }
    }
    result.test(position, environment, argTypes);
    return result;
  }

  @Override
  public String getAdamaType() {
    return "$<" + name + ">";
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeFunctional(name, overloads, style).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    for (final FunctionOverloadInstance fo : overloads) {
      fo.typing(environment);
    }
    for (var k = 0; k < overloads.size(); k++) {
      for (var j = k + 1; j < overloads.size(); j++) {
        overloads.get(k).testOverlap(overloads.get(j), environment);
      }
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_functional");
    writer.endObject();
  }
}
