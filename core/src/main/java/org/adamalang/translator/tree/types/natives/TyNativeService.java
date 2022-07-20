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
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.DefineService;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeService extends TyType implements DetailTypeHasMethods {
  public final DefineService service;
  public TyNativeService(final DefineService service) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.service = service;
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAdamaType() {
    return "service";
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
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeService(service).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("service");
    writer.writeObjectFieldIntro("service");
    writer.writeString(service.name.text);
    writeAnnotations(writer);
    writer.endObject();
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    DefineService.ServiceMethod method = service.methodsMap.get(name);
    if (method != null) {
      ArrayList<TyType> argTypes = new ArrayList<>();
      {
        TyType inputType = environment.rules.FindMessageStructure(method.inputTypeName.text, this, true).withPosition(service);
        argTypes.add(new TyNativeClient(TypeBehavior.ReadWriteNative, null, null).withPosition(service));
        argTypes.add(inputType);
      }
      TyType outputType = environment.rules.FindMessageStructure(method.outputTypeName.text, this, true);
      if (method.outputArrayExt != null) {
        outputType = new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, outputType, method.outputArrayExt);
      }
      outputType = outputType.withPosition(service);
      outputType = new TyNativeResult(TypeBehavior.ReadOnlyNativeValue, null, method.methodToken, new TokenizedItem<>(outputType)).withPosition(this);
      return new TyNativeFunctional(name,
          FunctionOverloadInstance.WRAP(
              new FunctionOverloadInstance(name, outputType, argTypes, true, false)),
          FunctionStyleJava.RemoteCall);
    }
    return null;
  }
}
