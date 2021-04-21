/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeAggregateFunctional;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailIndexLookup;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;

public class TyNativeList extends TyType implements DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    AssignmentViaNativeOnlyForSet, //
    DetailHasDeltaType, //
    DetailIndexLookup, //
    DetailComputeRequiresGet, //
    DetailTypeHasMethods {
  public static TyNativeList WRAP(final TyType type) {
    return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(type));
  }

  public final TyType elementType;
  public final Token listToken;
  public final Token readonlyToken;
  public final TokenizedItem<TyType> tokenElementType;

  public TyNativeList(final TypeBehavior behavior, final Token readonlyToken, final Token listToken, final TokenizedItem<TyType> tokenElementType) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.listToken = listToken;
    elementType = tokenElementType.item;
    this.tokenElementType = tokenElementType;
    ingest(listToken);
    ingest(elementType);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
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
  public String getDeltaType(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    if (resolved instanceof TyReactiveRecord) { return "DRecordList<" + ((TyReactiveRecord) resolved).getDeltaType(environment) + ">"; }
    return "DList<" + ((DetailHasDeltaType) resolved).getDeltaType(environment) + ">";
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(elementType, false);
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
    return IndexLookupStyle.ExpressionLookupMethod;
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
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, listToken).withPosition(this), new ArrayList<>(), true)),
          FunctionStyleJava.ExpressionThenArgs);
    }
    if ("toArray".equals(name)) {
      final var foi = new FunctionOverloadInstance("toArray", new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, tokenElementType.item, null).withPosition(this), new ArrayList<>(), true);
      foi.hiddenSuffixArgs.add("(Integer __n) -> (Object) (new " + tokenElementType.item.getJavaConcreteType(environment) + "[__n])");
      return new TyNativeFunctional("toArray", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.ExpressionThenArgs);
    }
    final var embedType = getEmbeddedType(environment);
    if (embedType != null && embedType instanceof DetailTypeHasMethods) {
      final var childMethod = ((DetailTypeHasMethods) embedType).lookupMethod(name, environment);
      if (childMethod != null) { return new TyNativeAggregateFunctional(embedType, childMethod); }
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeList(newBehavior, readonlyToken, listToken, new TokenizedItem<>(elementType.makeCopyWithNewPosition(position, newBehavior))).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    if (elementType instanceof DetailComputeRequiresGet) {
      return new TyNativeList(behavior, readonlyToken, listToken, new TokenizedItem<>(((DetailComputeRequiresGet) elementType).typeAfterGet(environment)));
    } else {
      return this;
    }
  }

  @Override
  public void typing(final Environment environment) {
    elementType.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_list");
    writer.writeObjectFieldIntro("type");
    elementType.writeTypeReflectionJson(writer);
    writer.endObject();
  }
}
