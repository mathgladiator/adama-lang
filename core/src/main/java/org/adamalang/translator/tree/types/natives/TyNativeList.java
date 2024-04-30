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
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.EmptyListConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetLists;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetMaybe;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeAggregateFunctional;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.reactive.TyReactiveRef;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.details.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeList extends TyType implements //
    DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    AssignmentViaNativeOnlyForSet, //
    DetailHasDeltaType, //
    DetailIndexLookup, //
    DetailComputeRequiresGet, //
    DetailInventDefaultValueExpression, //
    DetailTypeHasMethods {
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

  public static TyNativeList WRAP(final TyType type) {
    return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(type));
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(listToken);
    tokenElementType.emitBefore(yielder);
    elementType.emit(yielder);
    tokenElementType.emitAfter(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    elementType.format(formatter);
  }

  @Override
  public String getAdamaType() {
    return String.format("list<%s>", elementType.getAdamaType());
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
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeList(newBehavior, readonlyToken, listToken, new TokenizedItem<>(elementType.makeCopyWithNewPosition(position, newBehavior))).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    elementType.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_list");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    elementType.writeTypeReflectionJson(writer, source);
    writer.endObject();
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(elementType, false);
  }

  @Override
  public String getDeltaType(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    if (resolved instanceof TyReactiveRecord) {
      return "DRecordList<" + ((TyReactiveRecord) resolved).getDeltaType(environment) + ">";
    }
    if (resolved instanceof TyNativeMessage && ((TyNativeMessage) resolved).hasUniqueId()) {
      return "DRecordList<" + ((TyNativeMessage) resolved).getDeltaType(environment) + ">";
    }
    return "DList<" + ((DetailHasDeltaType) resolved).getDeltaType(environment) + ">";
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
      return new TyNativeFunctional("size", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, listToken).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("toArray".equals(name)) {
      final var foi = new FunctionOverloadInstance("toArray", new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, tokenElementType.item, null).withPosition(this), new ArrayList<>(), FunctionPaint.READONLY_NORMAL);
      TyType elementType = environment.rules.Resolve(tokenElementType.item, true);
      if (elementType != null) {
        foi.hiddenSuffixArgs.add("(Integer __n) -> (Object) (new " + elementType.getJavaConcreteType(environment) + "[__n])");
      }
      return new TyNativeFunctional("toArray", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("flatten".equals(name)) {
      TyType listElementType = getEmbeddedType(environment);
      if (RuleSetLists.IsNativeList(environment, listElementType, true) && listElementType instanceof DetailContainsAnEmbeddedType) {
        TyType itemType = ((DetailContainsAnEmbeddedType) listElementType).getEmbeddedType(environment);
        TyType resultType = new TyNativeList(TypeBehavior.ReadOnlyNativeValue,  null, null, new TokenizedItem<>(itemType)).withPosition(this);
        final var foi = new FunctionOverloadInstance("LibLists.flatten", resultType, new ArrayList<>(), FunctionPaint.READONLY_NORMAL);
        return new TyNativeFunctional("LibLists.flatten", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.InjectNameThenExpressionAndArgs);
      }
    }
    if ("manifest".equals(name)) {
      TyType listElementType = getEmbeddedType(environment);
      if (RuleSetMaybe.IsMaybe(environment, listElementType, true) && listElementType instanceof DetailContainsAnEmbeddedType) {
        TyType typeInMaybe = ((DetailContainsAnEmbeddedType) listElementType).getEmbeddedType(environment);
        TyType resultType = new TyNativeList(TypeBehavior.ReadOnlyNativeValue,  null, null, new TokenizedItem<>(typeInMaybe)).withPosition(this);
        final var foi = new FunctionOverloadInstance("LibLists.manifest", resultType, new ArrayList<>(), FunctionPaint.READONLY_CAST);
        foi.setThisType(this);
        return new TyNativeFunctional("LibLists.manifest", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.InjectNameThenExpressionAndArgs);
      }
    }
    if ("reverse".equals(name)) {
      TyType listElementType = getEmbeddedType(environment);
      TyType resultType = new TyNativeList(TypeBehavior.ReadOnlyNativeValue,  null, null, new TokenizedItem<>(listElementType)).withPosition(this);
      final var foi = new FunctionOverloadInstance("LibLists.reverse", resultType, new ArrayList<>(), FunctionPaint.READONLY_NORMAL);
      return new TyNativeFunctional("LibLists.reverse", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("skip".equals(name)) {
      TyType listElementType = getEmbeddedType(environment);
      TyType resultType = new TyNativeList(TypeBehavior.ReadOnlyNativeValue,  null, null, new TokenizedItem<>(listElementType)).withPosition(this);
      ArrayList<TyType> args = new ArrayList<>();
      args.add(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null));
      final var foi = new FunctionOverloadInstance("LibLists.skip", resultType, args, FunctionPaint.READONLY_NORMAL);
      return new TyNativeFunctional("LibLists.skip", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    if ("drop".equals(name)) {
      TyType listElementType = getEmbeddedType(environment);
      TyType resultType = new TyNativeList(TypeBehavior.ReadOnlyNativeValue,  null, null, new TokenizedItem<>(listElementType)).withPosition(this);
      ArrayList<TyType> args = new ArrayList<>();
      args.add(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null));
      final var foi = new FunctionOverloadInstance("LibLists.drop", resultType, args, FunctionPaint.READONLY_NORMAL);
      return new TyNativeFunctional("LibLists.drop", FunctionOverloadInstance.WRAP(foi), FunctionStyleJava.InjectNameThenExpressionAndArgs);
    }
    TyNativeFunctional extensionBeforeAggregate = environment.state.globals.findExtension(this, name);
    if (extensionBeforeAggregate != null) {
      return extensionBeforeAggregate;
    }
    final var embedType = getEmbeddedType(environment);
    if (embedType != null && embedType instanceof DetailTypeHasMethods) {
      final var childMethod = ((DetailTypeHasMethods) embedType).lookupMethod(name, environment);
      if (childMethod != null) {
        return new TyNativeAggregateFunctional(embedType, childMethod);
      }
    }
    return null;
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
  public Expression inventDefaultValueExpression(DocumentPosition forWhatExpression) {
    return new EmptyListConstant(null, tokenElementType);
  }
}
