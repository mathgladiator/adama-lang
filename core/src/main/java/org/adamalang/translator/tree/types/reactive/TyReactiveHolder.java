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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetMessages;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyReactiveHolder extends TyType implements //
    DetailTypeHasMethods, //
    DetailHasDeltaType, //
    AssignmentViaSetter, //
    DetailComputeRequiresGet {
  public final boolean readonly;
  public final String messageTypeName;
  public final TokenizedItem<Token> messageTypeToken;
  public final Token holderToken;
  private TyNativeMessage cached;

  public TyReactiveHolder(boolean readonly, final Token holderToken, final TokenizedItem<Token> messageTypeToken) {
    super(readonly ? TypeBehavior.ReadOnlyWithGet : TypeBehavior.ReadWriteWithSetGet);
    this.readonly = readonly;
    this.holderToken = holderToken;
    this.messageTypeToken = messageTypeToken;
    messageTypeName = messageTypeToken.item.text;
    ingest(holderToken);
    ingest(messageTypeToken.item);
    this.cached = null;
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    yielder.accept(holderToken);
    messageTypeToken.emitBefore(yielder);
    yielder.accept(messageTypeToken.item);
    messageTypeToken.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return "hold<" + messageTypeName + ">";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return "RxHolder<RTx" + messageTypeName + ">";
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyReactiveHolder(readonly, holderToken, messageTypeToken).withPosition(position);
  }

  private TyNativeMessage eval(Environment environment) {
    if (cached != null) {
      return cached;
    }
    TyType result = environment.rules.Resolve(new TyNativeRef(TypeBehavior.ReadWriteWithSetGet, null, messageTypeToken.item), false);
    if (RuleSetMessages.IsNativeMessage(environment, result, false)) {
      cached = (TyNativeMessage) result;
    }
    return cached;
  }

  @Override
  public void typing(Environment environment) {
    eval(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_holder");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("message_name");
    writer.writeString(messageTypeName);
    writer.endObject();
  }

  @Override
  public TyType typeAfterGet(Environment environment) {
    return eval(environment);
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    if ("write".equals(name)) {
      return new TyNativeFunctional("write", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("write", eval(environment), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }

  @Override
  public String getDeltaType(Environment environment) {
    TyNativeMessage msg = eval(environment);
    if (msg != null) {
      return msg.getDeltaType(environment);
    }
    return null;
  }
}
