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
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

import java.util.Map;
import java.util.function.Consumer;

public class ConvertMessage extends Expression {
  public final Token closeParen;
  public final Token closeType;
  public final Token convertToken;
  public final Expression expression;
  public final String newMessageType;
  public final Token newMessageTypeToken;
  public final Token openParen;
  public final Token openType;
  private MessageConversionStyle style;

  public ConvertMessage(final Token convertToken, final Token openType, final Token newMessageTypeToken, final Token closeType, final Token openParen, final Expression expression, final Token closeParen) {
    this.convertToken = convertToken;
    this.openType = openType;
    this.newMessageTypeToken = newMessageTypeToken;
    this.closeType = closeType;
    this.openParen = openParen;
    this.expression = expression;
    this.closeParen = closeParen;
    newMessageType = newMessageTypeToken.text;
    style = MessageConversionStyle.None;
    ingest(openType);
    ingest(expression);
    ingest(closeParen);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(convertToken);
    yielder.accept(openType);
    yielder.accept(newMessageTypeToken);
    yielder.accept(closeType);
    yielder.accept(openParen);
    expression.emit(yielder);
    yielder.accept(closeParen);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var preCopyType = environment.rules.FindMessageStructure(newMessageType, this, false);
    final var exprType = environment.rules.ResolvePtr(expression.typing(environment, null), false);
    if (preCopyType == null) {
      return null;
    }
    final var idealType = preCopyType.makeCopyWithNewPosition(this, TypeBehavior.ReadOnlyNativeValue);
    if (environment.rules.IsNativeArrayOfStructure(exprType, true)) {
      // X{]
      style = MessageConversionStyle.Multiple;
      if (environment.rules.CanStructureAProjectIntoStructureB(((DetailContainsAnEmbeddedType) exprType).getEmbeddedType(environment), idealType, false)) {
        return new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, idealType.makeCopyWithNewPosition(this, TypeBehavior.ReadOnlyNativeValue), null).withPosition(this);
      }
    } else if (environment.rules.IsNativeListOfStructure(exprType, true)) {
      // list<X>
      style = MessageConversionStyle.Multiple;
      if (environment.rules.CanStructureAProjectIntoStructureB(((DetailContainsAnEmbeddedType) exprType).getEmbeddedType(environment), idealType, false)) {
        return new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, idealType.makeCopyWithNewPosition(this, TypeBehavior.ReadOnlyNativeValue), null).withPosition(this);
      }
    } else if (environment.rules.IsStructure(exprType, true)) {
      // X
      style = MessageConversionStyle.Single;
      if (environment.rules.CanStructureAProjectIntoStructureB(exprType, idealType, false)) {
        return idealType;
      }
    } else if (environment.rules.IsMaybe(exprType, true)) {
      final var subExpr = environment.rules.ResolvePtr(environment.rules.ExtractEmbeddedType(exprType, false), false);
      // maybe<X>
      if (subExpr != null && environment.rules.IsStructure(subExpr, false)) {
        style = MessageConversionStyle.Maybe;
        if (environment.rules.CanStructureAProjectIntoStructureB(subExpr, idealType, false)) {
          return idealType;
        }
      } else {
        environment.rules.SignalConversionIssue(exprType, false);
      }
    } else {
      environment.rules.SignalConversionIssue(exprType, false);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (style == MessageConversionStyle.Multiple) {
      final var elementType = environment.rules.ExtractEmbeddedType(expression.cachedType, true);
      sb.append("Utility.convertMultiple(");
      expression.writeJava(sb, environment);
      sb.append(", (__n) -> new RTx").append(newMessageType).append("[__n], (__obj) -> ");
      writeNewMessage(sb, elementType, environment);
      sb.append(")");
    } else if (style == MessageConversionStyle.Single) {
      sb.append("Utility.convertSingle(");
      expression.writeJava(sb, environment);
      sb.append(", (__obj) -> ");
      writeNewMessage(sb, expression.cachedType, environment);
      sb.append(")");
    } else if (style == MessageConversionStyle.Maybe) {
      final var elementType = environment.rules.ExtractEmbeddedType(expression.cachedType, true);
      sb.append("Utility.convertMaybe(");
      expression.writeJava(sb, environment);
      sb.append(", (__obj) -> ");
      writeNewMessage(sb, elementType, environment);
      sb.append(")");
    }
  }

  private void writeNewMessage(final StringBuilder sb, final TyType elementType, final Environment environment) {
    final var idealType = environment.rules.FindMessageStructure(newMessageType, this, false);
    sb.append("new RTx").append(newMessageType).append("(");
    var first = true;
    final var scoped = environment.scope();
    scoped.define("__obj", elementType, false, this);
    for (final Map.Entry<String, FieldDefinition> entry : idealType.storage().fields.entrySet()) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      final var fieldLookup = new FieldLookup(new Lookup(Token.WRAP("__obj")), null, Token.WRAP(entry.getKey()));
      fieldLookup.typing(scoped, null);
      fieldLookup.writeJava(sb, scoped);
    }
    sb.append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
