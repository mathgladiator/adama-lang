/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.reactive;

import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

public class TyReactiveMaybe extends TyType implements DetailContainsAnEmbeddedType, //
    DetailComputeRequiresGet, //
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
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(maybeToken);
    tokenizedElementType.emitBefore(yielder);
    tokenizedElementType.item.emit(yielder);
    tokenizedElementType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("maybe<%s>", tokenizedElementType.item.getAdamaType());
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(tokenizedElementType.item, false);
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
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveMaybe(maybeToken, tokenizedElementType).withPosition(position);
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
    writer.writeObjectFieldIntro("type");
    tokenizedElementType.item.writeTypeReflectionJson(writer);
    writer.endObject();
  }
}
