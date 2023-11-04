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
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;

import java.util.function.Consumer;

public class TyNativeRef extends TyType implements //
    CanBeNativeArray, //
    DetailRequiresResolveCall //
{
  public final Token readonlyToken;
  public final String ref;
  public final Token refToken;

  public TyNativeRef(final TypeBehavior behavior, final Token readonlyToken, final Token refToken) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.refToken = refToken;
    ref = refToken.text;
    ingest(refToken);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(refToken);
  }

  @Override
  public void format(Formatter formatter) {
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
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeRef(newBehavior, readonlyToken, refToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    // handled by environment.rules.Revolve
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_ref");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("ref");
    writer.writeString(ref);
    writer.endObject();
  }

  @Override
  public TyType resolve(final Environment environment) {
    final var other = environment.document.types.get(ref);
    if (other != null) {
      return other.makeCopyWithNewPosition(other, behavior);
    }
    return null;
  }
}
