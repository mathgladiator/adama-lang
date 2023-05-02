/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.traits.IsKillable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

import java.util.function.Consumer;

public class TyReactiveMaybe extends TyType implements //
    DetailContainsAnEmbeddedType, //
    DetailComputeRequiresGet, //
    IsKillable, //
    AssignmentViaSetter //
{
  public final Token maybeToken;
  public final TokenizedItem<TyType> tokenizedElementType;

  public TyReactiveMaybe(final Token maybeToken, final TokenizedItem<TyType> elementType) {
    super(TypeBehavior.ReadWriteWithSetGet);
    this.maybeToken = maybeToken;
    tokenizedElementType = elementType;
    ingest(maybeToken);
    ingest(elementType.item);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    yielder.accept(maybeToken);
    tokenizedElementType.emitBefore(yielder);
    tokenizedElementType.item.emit(yielder);
    tokenizedElementType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("r<maybe<%s>>", tokenizedElementType.item.getAdamaType());
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    final var elementTypeFixed = getEmbeddedType(environment);
    return elementTypeFixed != null ? String.format("RxMaybe<%s>", elementTypeFixed.getJavaBoxType(environment)) : "RxMaybe<? /* TODO */>";
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(tokenizedElementType.item, false);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveMaybe(maybeToken, tokenizedElementType).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    environment.rules.Resolve(tokenizedElementType.item, false);
    tokenizedElementType.item.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_maybe");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    tokenizedElementType.item.writeTypeReflectionJson(writer);
    writer.endObject();
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    final var elementType = environment.rules.Resolve(tokenizedElementType.item, false);
    if (elementType instanceof TyReactiveRecord) {
      return new TyNativeMaybe(TypeBehavior.ReadWriteNative, null, maybeToken, new TokenizedItem<>(elementType));
    } else {
      return new TyNativeMaybe(TypeBehavior.ReadWriteNative, null, maybeToken, new TokenizedItem<>(((DetailComputeRequiresGet) elementType).typeAfterGet(environment)));
    }
  }
}
