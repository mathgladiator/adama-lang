/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.natives.TyNativeTable;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;

import java.util.function.Consumer;

/** for passing a table in via an argument */
public class TyTablePtr extends TyType implements DetailRequiresResolveCall {

  public final String nameTokenText;
  public final Token readonlyToken;
  public final Token tableToken;
  public final TokenizedItem<Token> nameToken;

  public TyTablePtr(final TypeBehavior behavior, final Token readonlyToken, final Token tableToken, final TokenizedItem<Token> nameToken) {
    super(behavior);
    this.tableToken = tableToken;
    this.readonlyToken = readonlyToken;
    this.nameToken = nameToken;
    nameTokenText = nameToken.item.text;
    ingest(tableToken);
    ingest(nameToken.item);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(tableToken);
    nameToken.emitBefore(yielder);
    yielder.accept(nameToken.item);
    nameToken.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return "table<" + nameTokenText + ">";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return resolve(environment).getJavaBoxType(environment);
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyTablePtr(newBehavior, readonlyToken, tableToken, nameToken).withPosition(position);
  }

  @Override
  public void typing(Environment environment) {
    resolve(environment).typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("table_ref");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("record_name");
    writer.writeString(nameTokenText);
    writer.endObject();
  }

  @Override
  public TyType resolve(Environment environment) {
    TyType subType = environment.document.types.get(nameTokenText);
    if (subType != null && subType instanceof TyNativeMessage) {
      return new TyNativeTable(behavior, readonlyToken, tableToken, nameToken);
    } else {
      return new TyReactiveTable(tableToken, nameToken);
    }
  }
}
