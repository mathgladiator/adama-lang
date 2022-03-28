/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.function.Consumer;

public class TyNativeReactiveRecordPtr extends TyType implements //
    AssignmentViaNative, //
    DetailTypeHasMethods, DetailContainsAnEmbeddedType //
{
  public final TyReactiveRecord source;

  public TyNativeReactiveRecordPtr(final TypeBehavior behavior, final TyReactiveRecord source) {
    super(behavior);
    this.source = source;
    ingest(source);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    source.emit(yielder);
  }

  @Override
  public String getAdamaType() {
    return source.getAdamaType();
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return source.getJavaBoxType(environment);
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return source.getJavaConcreteType(environment);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeReactiveRecordPtr(newBehavior, source).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    source.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_reactive_ptr");
    writeAnnotations(writer);
    writer.endObject();
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return source;
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    return source.lookupMethod(name, environment);
  }
}
