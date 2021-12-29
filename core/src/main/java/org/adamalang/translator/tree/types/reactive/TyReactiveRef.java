/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailSpecialReactiveRefResolve;

import java.util.function.Consumer;

public class TyReactiveRef extends TyType implements DetailRequiresResolveCall {
  public final String ref;
  public final Token refToken;

  public TyReactiveRef(final Token refToken) {
    super(TypeBehavior.ReadWriteWithSetGet);
    this.refToken = refToken;
    ref = refToken.text;
    ingest(refToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(refToken);
  }

  @Override
  public String getAdamaType() {
    return ref;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    throw new UnsupportedOperationException("the reference must be resolved");
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    throw new UnsupportedOperationException("the reference must be resolved");
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveRef(refToken).withPosition(position);
  }

  @Override
  public TyType resolve(final Environment environment) {
    final var type = environment.document.types.get(ref);
    if (type instanceof DetailSpecialReactiveRefResolve) { return ((DetailSpecialReactiveRefResolve) type).typeAfterReactiveRefResolve(environment); }
    return type;
  }

  @Override
  public void typing(final Environment environment) {
    // handled by environment.rules.Revolve
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_ref");
    writer.writeObjectFieldIntro("ref");
    writer.writeString(ref);
    writer.endObject();
  }
}
