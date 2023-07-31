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
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeResult extends TyType implements //
    DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    DetailHasDeltaType, //
    DetailInventDefaultValueExpression, AssignmentViaSetter, //
    DetailTypeHasMethods {
  public final Token resultToken;
  public final Token readonlyToken;
  public final TokenizedItem<TyType> tokenElementType;

  public TyNativeResult(final TypeBehavior behavior, final Token readonlyToken, final Token resultToken, final TokenizedItem<TyType> tokenElementType) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.resultToken = resultToken;
    this.tokenElementType = tokenElementType;
    ingest(resultToken);
    ingest(tokenElementType.item);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(resultToken);
    tokenElementType.emitBefore(yielder);
    tokenElementType.item.emit(yielder);
    tokenElementType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return String.format("result<%s>", tokenElementType.item.getAdamaType());
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    return String.format("NtResult<%s>", resolved.getJavaBoxType(environment));
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
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
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeResult(newBehavior, readonlyToken, resultToken, new TokenizedItem<>(tokenElementType.item.makeCopyWithNewPosition(position, newBehavior))).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    tokenElementType.item.typing(environment);
    environment.rules.Resolve(tokenElementType.item, false);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_result");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    tokenElementType.item.writeTypeReflectionJson(writer, source);
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    final var resolvedType = environment.rules.Resolve(tokenElementType.item, true);
    return "DResult<" + ((DetailHasDeltaType) resolvedType).getDeltaType(environment) + ">";
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
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new InjectExpression(this) {
      @Override
      public void writeJava(final StringBuilder sb, final Environment environment) {
        sb.append(getStringWhenValueNotProvided(environment));
      }
    };
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if ("has".equals(name)) {
      return new TyNativeFunctional("has", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("has", new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("failed".equals(name)) {
      return new TyNativeFunctional("failed", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("failed", new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("finished".equals(name)) {
      return new TyNativeFunctional("finished", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("finished", new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("code".equals(name)) {
      return new TyNativeFunctional("code", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("code", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("message".equals(name)) {
      return new TyNativeFunctional("message", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("message", new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("await".equals(name)) {
      return new TyNativeFunctional("await", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("await", new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, tokenElementType), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("as_maybe".equals(name)) {
      return new TyNativeFunctional("as_maybe", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("as_maybe", new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, tokenElementType), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return environment.state.globals.findExtension(this, name);
  }
}
