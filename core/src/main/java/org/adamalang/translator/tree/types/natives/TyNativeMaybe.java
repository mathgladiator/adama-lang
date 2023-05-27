/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeMaybe extends TyType implements //
    DetailContainsAnEmbeddedType, //
    DetailNativeDeclarationIsNotStandard, //
    DetailHasDeltaType, //
    DetailInventDefaultValueExpression, AssignmentViaSetter, //
    DetailTypeHasMethods {
  public final Token maybeToken;
  public final Token readonlyToken;
  public final TokenizedItem<TyType> tokenElementType;

  public TyNativeMaybe(final TypeBehavior behavior, final Token readonlyToken, final Token maybeToken, final TokenizedItem<TyType> tokenElementType) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.maybeToken = maybeToken;
    this.tokenElementType = tokenElementType;
    ingest(maybeToken);
    ingest(tokenElementType.item);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
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
  public String getJavaBoxType(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    return String.format("NtMaybe<%s>", resolved.getJavaBoxType(environment));
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
    return new TyNativeMaybe(newBehavior, readonlyToken, maybeToken, new TokenizedItem<>(tokenElementType.item.makeCopyWithNewPosition(position, newBehavior))).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    tokenElementType.item.typing(environment);
    environment.rules.Resolve(tokenElementType.item, false);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_maybe");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    tokenElementType.item.writeTypeReflectionJson(writer);
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    final var resolvedType = environment.rules.Resolve(tokenElementType.item, true);
    return "DMaybe<" + ((DetailHasDeltaType) resolvedType).getDeltaType(environment) + ">";
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
    if ("delete".equals(name)) {
      return new TyNativeFunctional("delete", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("size", null, new ArrayList<>(), FunctionPaint.NORMAL)), FunctionStyleJava.ExpressionThenArgs);    }
    if ("has".equals(name)) {
      return new TyNativeFunctional("has", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("has", new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("getOrDefaultTo".equals(name)) {
      ArrayList<TyType> args = new ArrayList<>();
      args.add(tokenElementType.item);
      return new TyNativeFunctional("getOrDefaultTo", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("getOrDefaultTo", tokenElementType.item, args, new FunctionPaint(true, true, false))), FunctionStyleJava.ExpressionThenArgs);
    }
    return environment.state.globals.findExtension(this, name);
  }
}
