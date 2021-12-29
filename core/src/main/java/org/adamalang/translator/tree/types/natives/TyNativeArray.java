/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeArray extends TyType implements //
    AssignmentViaNative, //
    DetailContainsAnEmbeddedType, //
    DetailHasDeltaType, //
    DetailIndexLookup, //
    DetailInventDefaultValueExpression, //
    DetailNativeDeclarationIsNotStandard, //
    DetailTypeHasMethods //
{
  public final Token arrayToken;
  public final TyType elementType;

  public TyNativeArray(final TypeBehavior behavior, final TyType elementType, final Token arrayToken) {
    super(behavior);
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
  public String getDeltaType(final Environment environment) {
    final var resolved = getEmbeddedType(environment);
    if (resolved instanceof TyReactiveRecord) { return "DRecordList<" + ((TyReactiveRecord) resolved).getDeltaType(environment) + ">"; }
    return "DList<" + ((DetailHasDeltaType) resolved).getDeltaType(environment) + ">";
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(elementType, true);
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
    if ("size".equals(name)) {
      return new TyNativeFunctionInternalFieldReplacement("length",
          FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("length", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, arrayToken).withPosition(this), new ArrayList<>(), false)), FunctionStyleJava.None);
    }
    return environment.state.globals.findExtension(this, name);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeArray(newBehavior, elementType.makeCopyWithNewPosition(position, newBehavior), arrayToken).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    elementType.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_array");
    writer.writeObjectFieldIntro("type");
    elementType.writeTypeReflectionJson(writer);
    writer.endObject();
  }
}
