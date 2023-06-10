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
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.function.Consumer;

/** wrap lazy wrap an existing type to require a .get() */
public class TyNativeLazyWrap extends TyType implements DetailComputeRequiresGet {

  public final TyType wrapped;

  public TyNativeLazyWrap(TyType wrapped) {
    super(wrapped.behavior);
    this.wrapped = wrapped;
    ingest(wrapped);
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    wrapped.emitInternal(yielder);
  }

  @Override
  public String getAdamaType() {
    return wrapped.getAdamaType();
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return wrapped.getJavaBoxType(environment);
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return wrapped.getJavaConcreteType(environment);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyNativeLazyWrap(wrapped.makeCopyWithNewPositionInternal(position, newBehavior));
  }

  @Override
  public void typing(Environment environment) {
    wrapped.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    wrapped.writeTypeReflectionJson(writer, source);
  }

  @Override
  public TyType typeAfterGet(Environment environment) {
    return wrapped;
  }
}
