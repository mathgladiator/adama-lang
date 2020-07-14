/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyNativeTable extends TyType implements //
    AssignmentViaSetter, //
    DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    DetailTypeHasMethods //
{
  public final String messageName;
  public final TokenizedItem<Token> messageNameToken;
  public final Token tableToken;

  public TyNativeTable(final Token tableToken, final TokenizedItem<Token> messageNameToken) {
    this.tableToken = tableToken;
    this.messageNameToken = messageNameToken;
    messageName = messageNameToken.item.text;
    ingest(tableToken);
    ingest(messageNameToken.item);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
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
  public TyType getEmbeddedType(final Environment environment) {
    TyType subtype = new TyNativeRef(messageNameToken.item);
    while (subtype instanceof DetailRequiresResolveCall) {
      subtype = ((DetailRequiresResolveCall) subtype).resolve(environment);
    }
    return subtype;
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
  public String getPatternWhenValueProvided(final Environment environment) {
    return "new " + getJavaBoxType(environment) + "(%s)";
  }

  @Override
  public String getStringWhenValueNotProvided(final Environment environment) {
    final var tt = environment.document.types.get(messageNameToken.item.text);
    var bridge = "";
    if (tt instanceof TyNativeMessage) {
      bridge = ((TyNativeMessage) tt).getBridge(environment);
    }
    return "new " + getJavaBoxType(environment) + "(" + bridge + ")";
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("size".equals(name)) {
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(messageNameToken.item).withPosition(this), new ArrayList<>(), true)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("delete".equals(name)) { return new TyNativeFunctional("delete", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", null, new ArrayList<>(), false)), FunctionStyleJava.ExpressionThenArgs); }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeTable(tableToken, messageNameToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    environment.rules.Resolve(new TyNativeRef(messageNameToken.item), false);
  }
}
