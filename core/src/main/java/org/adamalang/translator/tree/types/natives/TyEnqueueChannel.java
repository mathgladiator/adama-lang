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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.DetailNeverPublic;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyEnqueueChannel extends TyType implements //
    DetailTypeHasMethods, //
    DetailNeverPublic {
  public final TokenizedItem<TyType> tokenizedType;
  public final String channelName;

  public TyEnqueueChannel(final String channelName, final TokenizedItem<TyType> tokenizedType) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.channelName = channelName;
    this.tokenizedType = tokenizedType;
    ingest(tokenizedType.item);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException("internal types can't be emitted");
  }

  @Override
  public void format(Formatter formatter) {
    throw new UnsupportedOperationException("internal types can't be emitted");
  }

  @Override
  public String getAdamaType() {
    return "enqueue.channel<" + tokenizedType.item.getAdamaType() + ">";
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    throw new UnsupportedOperationException("TyEnqueueChannel does support assignment");
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    throw new UnsupportedOperationException("TyEnqueueChannel does support assignment");
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyEnqueueChannel(channelName, tokenizedType).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    tokenizedType.item.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("channelqueue");
    writer.writeObjectFieldIntro("channel");
    writer.writeString(channelName);
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    tokenizedType.item.writeTypeReflectionJson(writer, source);
    writer.endObject();
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("enqueue".equals(name)) {
      TyNativeVoid noResult = new TyNativeVoid();
      final var argTypes = new ArrayList<TyType>();
      argTypes.add(new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, null));
      argTypes.add(environment.rules.Resolve(tokenizedType.item, false));
      return new TyNativeFunctional("__enqueue", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__enqueue(\"" + channelName + "\", ", noResult, argTypes, FunctionPaint.NORMAL)), FunctionStyleJava.InjectNameThenArgsNoInitialParenthesis);
    }

    return null;
  }
}
