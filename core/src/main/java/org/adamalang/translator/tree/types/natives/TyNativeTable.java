/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
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
    environment.rules.Resolve(new TyNativeRef(behavior, null, messageNameToken.item), false);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
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
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, messageNameToken.item).withPosition(this), new ArrayList<>(), true, false)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("delete".equals(name)) {
      return new TyNativeFunctional("delete", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("delete", null, new ArrayList<>(), false, false)), FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }
}
