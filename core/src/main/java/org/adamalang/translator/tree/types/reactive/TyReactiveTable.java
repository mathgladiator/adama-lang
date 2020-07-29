/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.reactive;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyReactiveTable extends TyType implements //
    DetailContainsAnEmbeddedType, //
    DetailTypeHasMethods {
  public final String recordName;
  public final TokenizedItem<Token> recordNameToken;
  public final Token tableToken;

  public TyReactiveTable(final Token tableToken, final TokenizedItem<Token> recordNameToken) {
    super(TypeBehavior.ReadWriteWithSetGet);
    this.tableToken = tableToken;
    this.recordNameToken = recordNameToken;
    recordName = recordNameToken.item.text;
    ingest(tableToken);
    ingest(recordNameToken.item);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(tableToken);
    recordNameToken.emitBefore(yielder);
    yielder.accept(recordNameToken.item);
    recordNameToken.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("table<%s>", recordName);
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    TyType subtype = new TyReactiveRef(recordNameToken.item);
    while (subtype instanceof DetailRequiresResolveCall) {
      subtype = ((DetailRequiresResolveCall) subtype).resolve(environment);
    }
    return subtype;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("RxTable<RTx%s>", recordName);
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("size".equals(name)) {
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, recordNameToken.item).withPosition(this), new ArrayList<>(), true)),
          FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveTable(tableToken, recordNameToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    environment.rules.Resolve(new TyReactiveRef(recordNameToken.item), false);
  }
}
