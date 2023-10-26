/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.ReflectionSource;
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
    final var elementTypeFixed = environment.rules.Resolve(getEmbeddedType(environment), true);
    String primary = elementTypeFixed.getJavaBoxType(environment);
    String secondary = null;
    if (elementTypeFixed instanceof DetailComputeRequiresGet) {
      secondary = environment.rules.Resolve(((DetailComputeRequiresGet) elementTypeFixed).typeAfterGet(environment), false).getJavaBoxType(environment);
    } else {
      secondary = primary;
    }
    return String.format("RxMaybe<%s,%s>", primary, secondary);
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
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_maybe");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    tokenizedElementType.item.writeTypeReflectionJson(writer, source);
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
