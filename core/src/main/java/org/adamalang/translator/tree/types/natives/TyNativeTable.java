/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeTable extends TyType implements //
    AssignmentViaSetter, //
    DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    DetailTypeHasMethods //
{
  public final String messageName;
  public final TokenizedItem<Token> messageNameToken;
  public final Token readonlyToken;
  public final Token tableToken;

  public TyNativeTable(final TypeBehavior behavior, final Token readonlyToken, final Token tableToken, final TokenizedItem<Token> messageNameToken) {
    super(behavior);
    this.tableToken = tableToken;
    this.readonlyToken = readonlyToken;
    this.messageNameToken = messageNameToken;
    messageName = messageNameToken.item.text;
    ingest(tableToken);
    ingest(messageNameToken.item);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(tableToken);
    messageNameToken.emitBefore(yielder);
    yielder.accept(messageNameToken.item);
    messageNameToken.emitAfter(yielder);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  public String getAdamaType() {
    return String.format("table<%s>", messageName);
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("NtTable<RTx%s>", messageName);
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeTable(newBehavior, readonlyToken, tableToken, messageNameToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    environment.rules.IsNativeMessage(environment.rules.Resolve(new TyNativeRef(behavior, null, messageNameToken.item), false), false);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_table");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("record_name");
    writer.writeString(messageName);
    writer.endObject();
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    TyType subtype = new TyNativeRef(behavior, null, messageNameToken.item);
    while (subtype instanceof DetailRequiresResolveCall) {
      subtype = ((DetailRequiresResolveCall) subtype).resolve(environment);
    }
    return subtype;
  }

  @Override
  public String getPatternWhenValueProvided(final Environment environment) {
    return "new " + getJavaBoxType(environment) + "(%s)";
  }

  @Override
  public String getStringWhenValueNotProvided(final Environment environment) {
    return "new " + getJavaBoxType(environment) + "(() -> new RTx" + messageNameToken.item.text + "())";
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("size".equals(name)) {
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, messageNameToken.item).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("to_csv".equals(name)) {
      TyNativeMessage messageType = (TyNativeMessage) getEmbeddedType(environment);
      if (messageType.storage.isCommaSeperateValueEnabled()) {
        return new TyNativeFunctional("to_csv", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("RTx" + messageType.name + ".__to_csv", new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, messageNameToken.item).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.InjectNameThenExpressionAndArgs);
      } else {
        environment.document.createError(this, "to_csv function is only available on a table when " + messageType.name + " has @csv enabled");
      }
    }
    if ("delete".equals(name)) {
      return new TyNativeFunctional("delete", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("delete", null, new ArrayList<>(), FunctionPaint.NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }
}
