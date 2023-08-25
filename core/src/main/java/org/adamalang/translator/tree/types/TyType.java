/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.common.Typable;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;

import java.util.function.Consumer;

public abstract class TyType extends DocumentPosition implements Typable {
  public final TypeBehavior behavior;
  private TypeAnnotation annotation;

  public TyType(final TypeBehavior behavior) {
    this.behavior = behavior;
    this.annotation = null;
  }

  public abstract void emitInternal(Consumer<Token> yielder);

  public void emit(Consumer<Token> yielder) {
    emitInternal(yielder);
    if (annotation != null) {
      annotation.emit(yielder);
    }
  }

  public abstract String getAdamaType();

  public abstract String getJavaBoxType(Environment environment);

  public abstract String getJavaConcreteType(Environment environment);

  public abstract TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior);

  public String getJavaDefaultValue(Environment environment, DocumentPosition position) {
    TyType self = environment.rules.Resolve(this, true);
    if (self instanceof DetailInventDefaultValueExpression) {
      StringBuilder sb = new StringBuilder();
      ((DetailInventDefaultValueExpression) self).inventDefaultValueExpression(position).writeJava(sb, environment);
      return sb.toString();
    }
    if (self instanceof DetailNativeDeclarationIsNotStandard) {
      return ((DetailNativeDeclarationIsNotStandard) self).getStringWhenValueNotProvided(environment);
    }
    throw new UnsupportedOperationException("failed to compute the default java value for:" + self.getAdamaType());
  }

  public TyType makeCopyWithNewPosition(DocumentPosition position, TypeBehavior newBehavior) {
    TyType copy = makeCopyWithNewPositionInternal(position, newBehavior);
    if (annotation != null) {
      copy.annotation = annotation;
    }
    return copy;
  }

  public abstract void typing(Environment environment);

  public void writeAnnotations(JsonStreamWriter writer) {
    if (annotation != null) {
      writer.writeObjectFieldIntro("annotations");
      writer.beginArray();
      for (TokenizedItem<TypeAnnotation.Annotation> token : annotation.annotations) {
        if (token.item.equals != null) {
          writer.beginObject();
          writer.writeObjectFieldIntro(token.item.name.text);
          writer.writeToken(token.item.value);
          writer.endObject();
        } else {
          writer.writeString(token.item.name.text);
        }
      }
      writer.endArray();
    }
  }

  public abstract void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source);

  public TyType withPosition(final DocumentPosition position) {
    reset();
    ingest(position);
    return this;
  }

  public void annotate(TypeAnnotation annotation) {
    this.annotation = annotation;
  }

  @Override
  public TyType getType() {
    return this;
  }
}
