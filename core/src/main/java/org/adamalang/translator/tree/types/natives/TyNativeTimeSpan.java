/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.TimeSpanConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

/** type for a native time span measured in seconds */
public class TyNativeTimeSpan extends TySimpleNative implements //
    IsNativeValue, //
    IsOrderable, //
    DetailHasDeltaType, //
    DetailTypeHasMethods, //
    AssignmentViaNative {
  public final Token readonlyToken;
  public final Token token;

  public TyNativeTimeSpan(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "NtTimeSpan", "NtTimeSpan");
    this.readonlyToken = readonlyToken;
    this.token = token;
    ingest(token);
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "datetime";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyNativeTimeSpan(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("timespan");
    writer.endObject();
  }

  @Override
  public String getDeltaType(Environment environment) {
    return "DTimeSpan";
  }

  @Override
  public Expression inventDefaultValueExpression(DocumentPosition forWhatExpression) {
    return new TimeSpanConstant(0, token);
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    if ("seconds".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("seconds", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("seconds", new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, Token.WRAP("readonly"), null).withPosition(this), new ArrayList<>(), true, false)), FunctionStyleJava.None);
    }
    return environment.state.globals.findExtension(this, name);
  }
}
