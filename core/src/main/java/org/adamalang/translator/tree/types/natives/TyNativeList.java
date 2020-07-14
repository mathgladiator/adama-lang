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
import org.adamalang.translator.tree.types.natives.functions.TyNativeAggregateFunctional;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailIndexLookup;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;

public class TyNativeList extends TyType implements DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    AssignmentViaNativeOnlyForSet, //
    DetailHasBridge, //
    DetailIndexLookup, //
    DetailComputeRequiresGet, //
    DetailTypeHasMethods {
  public static TyNativeList WRAP(final TyType type) {
    return new TyNativeList(null, new TokenizedItem<>(type));
  }

  public final TyType elementType;
  public final Token listToken;
  public final TokenizedItem<TyType> tokenElementType;

  public TyNativeList(final Token listToken, final TokenizedItem<TyType> tokenElementType) {
    this.listToken = listToken;
    elementType = tokenElementType.item;
    this.tokenElementType = tokenElementType;
    ingest(listToken);
    ingest(elementType);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(listToken);
    tokenElementType.emitBefore(yielder);
    elementType.emit(yielder);
    tokenElementType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("list<%s>", elementType.getAdamaType());
  }

  @Override
  public String getBridge(final Environment environment) {
    final var resolvedType = environment.rules.Resolve(elementType, true);
    return String.format("NativeBridge.WRAP_LIST(%s)", ((DetailHasBridge) resolvedType).getBridge(environment));
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
    final var resolved = getEmbeddedType(environment);
    return String.format("NtList<%s>", resolved.getJavaBoxType(environment));
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public IndexLookupStyle getLookupStyle(final Environment environment) {
    return IndexLookupStyle.Method;
  }

  @Override
  public String getPatternWhenValueProvided(final Environment environment) {
    return "%s";
  }

  @Override
  public String getStringWhenValueNotProvided(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    return String.format("new EmptyNtList<%s>()", resolved.getJavaBoxType(environment));
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("size".equals(name)) {
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(listToken).withPosition(this), new ArrayList<>(), true)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("toArray".equals(name)) {
      return new TyNativeFunctional("toArray", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("insert", new TyNativeArray(tokenElementType.item, null).makeCopyWithNewPosition(this), new ArrayList<>(), true)),
          FunctionStyleJava.ExpressionThenArgs);
    }
    final var embedType = getEmbeddedType(environment);
    if (embedType != null && embedType instanceof DetailTypeHasMethods) {
      final var childMethod = ((DetailTypeHasMethods) embedType).lookupMethod(name, environment);
      if (childMethod != null) { return new TyNativeAggregateFunctional(embedType, childMethod); }
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeList(listToken, new TokenizedItem<>(elementType.makeCopyWithNewPosition(position))).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    if (elementType instanceof DetailComputeRequiresGet) {
      return new TyNativeList(listToken, new TokenizedItem<>(((DetailComputeRequiresGet) elementType).typeAfterGet(environment)));
    } else {
      return this;
    }
  }

  @Override
  public void typing(final Environment environment) {
    elementType.typing(environment);
  }
}
