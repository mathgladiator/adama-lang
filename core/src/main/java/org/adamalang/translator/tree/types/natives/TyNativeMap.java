/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetTable;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeMap extends TyType implements //
    AssignmentViaSetter, //
    DetailHasDeltaType, //
    DetailTypeHasMethods, //
    DetailNativeDeclarationIsNotStandard, //
    DetailContainsAnEmbeddedType, //
    IsMap //
{
  public final Token readonlyToken;
  public final Token closeThing;
  public final Token commaToken;
  public final TyType domainType;
  public final Token mapToken;
  public final Token openThing;
  public final TyType rangeType;

  public TyNativeMap(final TypeBehavior behavior, final Token readonlyToken, final Token mapToken, final Token openThing, final TyType domainType, final Token commaToken, final TyType rangeType, final Token closeThing) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.mapToken = mapToken;
    this.openThing = openThing;
    this.domainType = domainType;
    this.commaToken = commaToken;
    this.rangeType = rangeType;
    this.closeThing = closeThing;
    ingest(mapToken);
    ingest(closeThing);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(mapToken);
    yielder.accept(openThing);
    domainType.emit(yielder);
    yielder.accept(commaToken);
    rangeType.emit(yielder);
    yielder.accept(closeThing);
  }

  @Override
  public String getAdamaType() {
    return "map<" + domainType.getAdamaType() + "," + rangeType.getAdamaType() + ">";
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    final var dt = getDomainType(environment);
    final var rt = getRangeType(environment);
    return "NtMap<" + dt.getJavaBoxType(environment) + "," + rt.getJavaBoxType(environment) + ">";
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType getDomainType(final Environment environment) {
    return environment.rules.Resolve(domainType, false);
  }

  @Override
  public TyType getRangeType(final Environment environment) {
    return environment.rules.Resolve(rangeType, false);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeMap(newBehavior, readonlyToken, mapToken, openThing, domainType, commaToken, rangeType, closeThing).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    domainType.typing(environment);
    rangeType.typing(environment);
    final var resolvedDomainType = environment.rules.Resolve(domainType, false);
    if (resolvedDomainType != null && !(resolvedDomainType instanceof CanBeMapDomain)) {
      environment.document.createError(this, String.format("The domain type '%s' is not an appropriate.", resolvedDomainType.getAdamaType()), "TyNativeMap");
    }
    final var resolvedRangeType = environment.rules.Resolve(rangeType, false);
    if (RuleSetTable.IsTable(environment, resolvedRangeType, true)) {
      environment.document.createError(this, String.format("The range type '%s' is not an appropriate for a map.", resolvedRangeType.getAdamaType()), "TyNativeMap");
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_map");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("domain");
    domainType.writeTypeReflectionJson(writer, source);
    writer.writeObjectFieldIntro("range");
    rangeType.writeTypeReflectionJson(writer, source);
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DMap<" + domainType.getJavaBoxType(environment) + "," + ((DetailHasDeltaType) rangeType).getDeltaType(environment) + ">";
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
    if ("insert".equals(name)) {
      final var args = new ArrayList<TyType>();
      args.add(this);
      return new TyNativeFunctional("insert", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("insert", this, args, FunctionPaint.CAST_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("remove".equals(name)) {
      final var args = new ArrayList<TyType>();
      args.add(environment.rules.Resolve(domainType, false));
      TyType returnType = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(rangeType)).withPosition(this);
      return new TyNativeFunctional("remove", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("remove", returnType, args, FunctionPaint.CAST_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("size".equals(name)) {
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, mapToken).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("min".equals(name)) {
      return new TyNativeFunctional("min", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("min", getCommonQueryResultType(environment), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("max".equals(name)) {
      return new TyNativeFunctional("max", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("max", getCommonQueryResultType(environment), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return environment.state.globals.findExtension(this, name);
  }

  private TyType getCommonQueryResultType(Environment environment) {
    return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(getEmbeddedType(environment))).withPosition(this);
  }

  @Override
  public TyType getEmbeddedType(Environment environment) {
    return new TyNativePair(TypeBehavior.ReadOnlyNativeValue, null, null, null, environment.rules.Resolve(domainType, false), null, environment.rules.Resolve(rangeType, false), null);
  }
}
