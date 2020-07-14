/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailIndexLookup;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;

public class TyNativeArray extends TyType implements //
    AssignmentViaNative, //
    DetailContainsAnEmbeddedType, //
    DetailHasBridge, //
    DetailIndexLookup, //
    DetailNativeDeclarationIsNotStandard, //
    DetailTypeHasMethods //
{
  public final Token arrayToken;
  public final TyType elementType;

  public TyNativeArray(final TyType elementType, final Token arrayToken) {
    this.elementType = elementType;
    this.arrayToken = arrayToken;
    ingest(elementType);
    ingest(arrayToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    elementType.emit(yielder);
    yielder.accept(arrayToken);
  }

  @Override
  public String getAdamaType() {
    return String.format("%s[]", elementType.getAdamaType());
  }

  @Override
  public String getBridge(final Environment environment) {
    final var resolvedType = environment.rules.Resolve(elementType, true);
    return String.format("NativeBridge.WRAP_ARRAY(%s)", ((DetailHasBridge) resolvedType).getBridge(environment));
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    var subtype = elementType;
    while (subtype instanceof DetailRequiresResolveCall) {
      subtype = ((DetailRequiresResolveCall) subtype).resolve(environment);
    }
    return subtype;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("%s[]", getEmbeddedType(environment).getJavaConcreteType(environment));
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public IndexLookupStyle getLookupStyle(final Environment environment) {
    return IndexLookupStyle.UtilityFunction;
  }

  @Override
  public String getPatternWhenValueProvided(final Environment environment) {
    return "%s";
  }

  @Override
  public String getStringWhenValueNotProvided(final Environment environment) {
    return "new " + getJavaConcreteType(environment) + "{}";
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("size".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("length", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("length", new TyNativeInteger(arrayToken).withPosition(this), new ArrayList<>(), false)),
          FunctionStyleJava.None);
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeArray(elementType.makeCopyWithNewPosition(position), arrayToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    elementType.typing(environment);
  }
}
