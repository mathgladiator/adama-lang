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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.DynamicNullConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeJson extends TySimpleNative implements //
    DetailHasDeltaType, //
    DetailTypeHasMethods, //
    AssignmentViaNative //
{
  public final Token readonlyToken;
  public final Token token;

  public TyNativeJson(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "NtJson", "NtJson", -1);
    this.readonlyToken = readonlyToken;
    this.token = token;
    ingest(token);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(token);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  public String getAdamaType() {
    return "json";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeJson(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("json");
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DDynamic";
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    if ("to_dynamic".equals(name)) {
      return new TyNativeFunctional("to_dynamic", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("to_dynamic", new TyNativeDynamic(behavior, readonlyToken, token), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("deref".equals(name)) {
      ArrayList<FunctionOverloadInstance> versions = new ArrayList<>();
      {
        ArrayList<TyType> args = new ArrayList<>();
        args.add(new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, token));
        versions.add(new FunctionOverloadInstance("deref", new TyNativeJson(behavior, readonlyToken, token), args, FunctionPaint.READONLY_NORMAL));
      }
      {
        ArrayList<TyType> args = new ArrayList<>();
        args.add(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, token));
        versions.add(new FunctionOverloadInstance("deref", new TyNativeJson(behavior, readonlyToken, token), args, FunctionPaint.READONLY_NORMAL));
      }
      return new TyNativeFunctional("next", versions, FunctionStyleJava.ExpressionThenArgs);
    }
    if ("to_b".equals(name)) {
      TyType retType = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, token, new TokenizedItem<>(new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null)));
      return new TyNativeFunctional("to_b", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("to_b", retType, new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("to_i".equals(name)) {
      TyType retType = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, token, new TokenizedItem<>(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
      return new TyNativeFunctional("to_i", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("to_i", retType, new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("to_s".equals(name)) {
      TyType retType = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, token, new TokenizedItem<>(new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null)));
      return new TyNativeFunctional("to_i", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("to_i", retType, new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return environment.state.globals.findExtension(this, name);
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new DynamicNullConstant(Token.WRAP("null")).withPosition(forWhatExpression);
  }
}
