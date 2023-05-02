/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyReactiveText extends TyType implements //
    DetailHasDeltaType, //
    AssignmentViaSetter, //
    DetailTypeHasMethods {
  public final Token textToken;

  public TyReactiveText(final Token textToken) {
    super(TypeBehavior.ReadWriteWithSetGet);
    this.textToken = textToken;
    ingest(textToken);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    yielder.accept(textToken);
  }

  @Override
  public String getAdamaType() {
    return "text";
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return "RxText";
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return "RxText";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveText(textToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_text");
    writeAnnotations(writer);
    writer.endObject();
  }

  @Override
  public String getDeltaType(Environment environment) {
    return "DText";
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    ArrayList<TyType> args = new ArrayList<>();
    if ("append".equals(name)) {
      args.add(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, textToken).withPosition(this));
      args.add(new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, textToken).withPosition(this));
      return new TyNativeFunctional("append", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("append", new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, textToken).withPosition(this), args, true, false, false)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("get".equals(name)) {
      return new TyNativeFunctional("append", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("append", new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, textToken).withPosition(this), args, true, false, false)), FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }
}
