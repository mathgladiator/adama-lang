/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetTable;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsKillable;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyReactiveMap extends TyType implements //
    DetailTypeHasMethods, //
    IsMap, //
    IsKillable, //
    DetailContainsAnEmbeddedType, //
    DetailHasDeltaType {
  public final Token closeThing;
  public final Token commaToken;
  public final TyType domainType;
  public final Token mapToken;
  public final Token openThing;
  public final TyType rangeType;

  public TyReactiveMap(final Token mapToken, final Token openThing, final TyType domainType, final Token commaToken, final TyType rangeType, final Token closeThing) {
    super(TypeBehavior.ReadWriteWithSetGet);
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
  public void emitInternal(Consumer<Token> yielder) {
    yielder.accept(mapToken);
    yielder.accept(openThing);
    domainType.emit(yielder);
    yielder.accept(commaToken);
    rangeType.emit(yielder);
    yielder.accept(closeThing);
  }

  @Override
  public void format(Formatter formatter) {
    domainType.format(formatter);
    rangeType.format(formatter);
  }

  @Override
  public String getAdamaType() {
    return "r<map<" + domainType.getAdamaType() + "," + rangeType.getAdamaType() + ">>";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return "RxMap<" + getDomainType(environment).getJavaBoxType(environment) + "," + getRangeType(environment).getJavaBoxType(environment) + ">";
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveMap(mapToken, openThing, domainType, commaToken, rangeType, closeThing).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    domainType.typing(environment);
    rangeType.typing(environment);
    final var resolvedDomainType = environment.rules.Resolve(domainType, false);
    if (resolvedDomainType != null && !(resolvedDomainType instanceof CanBeMapDomain)) {
      environment.document.createError(this, String.format("The domain type '%s' is not an appropriate.", resolvedDomainType.getAdamaType()));
    }
    final var resolvedRangeType = environment.rules.Resolve(rangeType, false);
    if (RuleSetTable.IsTable(environment, resolvedRangeType, true)) {
      environment.document.createError(this, String.format("The range type '%s' is not an appropriate for a map.", resolvedRangeType.getAdamaType()));
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_map");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("domain");
    domainType.writeTypeReflectionJson(writer, source);
    writer.writeObjectFieldIntro("range");
    rangeType.writeTypeReflectionJson(writer, source);
    writer.endObject();
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
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("size".equals(name)) {
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, mapToken).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("remove".equals(name)) {
      ArrayList<TyType> args = new ArrayList<>();
      args.add(domainType);
      return new TyNativeFunctional("remove", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("remove", new TyNativeVoid().withPosition(this), args, FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("has".equals(name)) {
      ArrayList<TyType> args = new ArrayList<>();
      args.add(domainType);
      return new TyNativeFunctional("remove", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("remove", new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, mapToken).withPosition(this), args, FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("clear".equals(name)) {
      return new TyNativeFunctional("clear", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("clear", new TyNativeVoid().withPosition(this), new ArrayList<>(), FunctionPaint.NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return null;
  }

  @Override
  public String getDeltaType(Environment environment) {
    String domainBox = getDomainType(environment).getJavaBoxType(environment);
    var range = getRangeType(environment);
    if (range instanceof DetailComputeRequiresGet) {
      range = ((DetailComputeRequiresGet) range).typeAfterGet(environment);
    }
    return "DMap<" + domainBox + "," + ((DetailHasDeltaType) range).getDeltaType(environment) + ">";
  }

  @Override
  public TyType getEmbeddedType(Environment environment) {
    return new TyNativePair(TypeBehavior.ReadOnlyNativeValue, null, null, null, environment.rules.Resolve(domainType, false), null, environment.rules.Resolve(rangeType, false), null);
  }
}
