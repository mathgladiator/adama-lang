/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;

import java.util.function.Consumer;

public class TyNativeVoid extends TyType {
  public TyNativeVoid() {
    super(TypeBehavior.ReadOnlyNativeValue);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
  }

  @Override
  public String getAdamaType() {
    return "void";
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return "void";
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return "void";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return this;
  }

  @Override
  public void typing(final Environment environment) {
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_void");
    writeAnnotations(writer);
    writer.endObject();
  }
}
