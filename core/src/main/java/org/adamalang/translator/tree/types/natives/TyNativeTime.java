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
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.TimeConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.traits.DetailCanExtractForUnique;
import org.adamalang.translator.tree.types.traits.IsCSVCompatible;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

/** Type for native time within a day at the precision of a minute */
public class TyNativeTime extends TySimpleNative implements //
    IsNativeValue, //
    IsOrderable, //
    DetailHasDeltaType, //
    DetailTypeHasMethods, //
    DetailCanExtractForUnique, //
    IsCSVCompatible, //
    AssignmentViaNative {
  public final Token readonlyToken;
  public final Token token;

  public TyNativeTime(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "NtTime", "NtTime", 64);
    this.readonlyToken = readonlyToken;
    this.token = token;
    ingest(token);
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
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
    return "time";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyNativeTime(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("time");
    writer.endObject();
  }

  @Override
  public String getDeltaType(Environment environment) {
    return "DTime";
  }

  @Override
  public Expression inventDefaultValueExpression(DocumentPosition forWhatExpression) {
    return new TimeConstant(0, 0, token);
  }


  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    if ("hour".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("hour", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("hour", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, Token.WRAP("readonly"), null).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.None);
    } else if ("minute".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("minute", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("minute", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, Token.WRAP("readonly"), null).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.None);
    }
    return environment.state.globals.findExtension(this, name);
  }
}
