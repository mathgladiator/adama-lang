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
package org.adamalang.translator.tree.types;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.Formatter;
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

  public abstract void format(Formatter formatter);

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
