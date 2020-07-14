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
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyNativeMaybe extends TyType implements DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    DetailHasBridge, //
    AssignmentViaSetter, //
    DetailTypeHasMethods {
  public final Token maybeToken;
  public final TokenizedItem<TyType> tokenElementType;

  public TyNativeMaybe(final Token maybeToken, final TokenizedItem<TyType> tokenElementType) {
    this.maybeToken = maybeToken;
    this.tokenElementType = tokenElementType;
    ingest(maybeToken);
    ingest(tokenElementType.item);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(maybeToken);
    tokenElementType.emitBefore(yielder);
    tokenElementType.item.emit(yielder);
    tokenElementType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("maybe<%s>", tokenElementType.item.getAdamaType());
  }

  @Override
  public String getBridge(final Environment environment) {
    final var resolvedType = environment.rules.Resolve(tokenElementType.item, true);
    return String.format("NativeBridge.WRAP_MAYBE(%s)", ((DetailHasBridge) resolvedType).getBridge(environment));
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    var subtype = tokenElementType.item;
    while (subtype instanceof DetailRequiresResolveCall) {
      subtype = ((DetailRequiresResolveCall) subtype).resolve(environment);
    }
    return subtype;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    return String.format("NtMaybe<%s>", resolved.getJavaBoxType(environment));
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
    return "new " + getJavaBoxType(environment) + "()";
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("delete".equals(name)) { return new TyNativeFunctional("delete", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", null, new ArrayList<>(), false)), FunctionStyleJava.ExpressionThenArgs); }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeMaybe(maybeToken, new TokenizedItem<>(tokenElementType.item.makeCopyWithNewPosition(position))).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    tokenElementType.item.typing(environment);
  }
}
