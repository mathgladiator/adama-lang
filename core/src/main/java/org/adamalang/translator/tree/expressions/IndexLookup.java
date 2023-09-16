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

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetMap;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetMaybe;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailIndexLookup;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;

import java.util.function.Consumer;

/**
 * return a maybe type from an index lookup. The maybe forces a range check, so it is always valid.
 */
public class IndexLookup extends Expression {
  public final Expression arg;
  public final Token bracketCloseToken;
  public final Token bracketOpenToken;
  public final Expression expression;
  private IndexLookupStyle lookupStyle;
  private String castArg;

  public IndexLookup(final Expression expression, final Token bracketOpenToken, final Expression arg, final Token bracketCloseToken) {
    this.expression = expression;
    this.bracketOpenToken = bracketOpenToken;
    this.arg = arg;
    this.bracketCloseToken = bracketCloseToken;
    this.ingest(expression);
    this.ingest(arg);
    this.ingest(bracketCloseToken);
    lookupStyle = IndexLookupStyle.Unknown;
    this.castArg = null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    expression.emit(yielder);
    yielder.accept(bracketOpenToken);
    arg.emit(yielder);
    yielder.accept(bracketCloseToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    // come up with a more specific form
    final var typeExpr = expression.typing(environment, null /* no suggestion */);
    TyType resultType = null;
    if (environment.rules.IsMap(typeExpr)) {
      final var mapType = (IsMap) typeExpr;
      final var typeArg = arg.typing(environment.scopeWithComputeContext(ComputeContext.Computation), mapType.getDomainType(environment));
      if (environment.state.isContextAssignment() && RuleSetMap.IsReactiveMap(environment, typeExpr)) {
        TyType domainType = mapType.getDomainType(environment);
        if (domainType instanceof TyNativeLong && typeArg instanceof TyNativeInteger) {
          castArg = "long";
        }
        lookupStyle = IndexLookupStyle.ExpressionGetOrCreateMethod;
        if (environment.rules.CanTypeAStoreTypeB(mapType.getDomainType(environment), typeArg, StorageTweak.None, false)) {
          resultType = mapType.getRangeType(environment);
        }
      } else {
        lookupStyle = IndexLookupStyle.ExpressionLookupMethod;
        if (environment.rules.CanTypeAStoreTypeB(mapType.getDomainType(environment), typeArg, StorageTweak.None, false)) {
          resultType = new TyNativeMaybe(TypeBehavior.ReadWriteWithSetGet, null, null, new TokenizedItem<>(mapType.getRangeType(environment))).withPosition(this);
        }
      }
    } else {
      environment.rules.IsIterable(typeExpr, false);
      if (typeExpr instanceof DetailIndexLookup) {
        lookupStyle = ((DetailIndexLookup) typeExpr).getLookupStyle(environment);
      }
      final var typeArg = arg.typing(environment.scopeWithComputeContext(ComputeContext.Computation), new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null));
      RuleSetMaybe.IsMaybeIntegerOrJustInteger(environment, typeArg, false);
      if (typeExpr != null && typeExpr instanceof DetailContainsAnEmbeddedType) {
        final var elementType = ((DetailContainsAnEmbeddedType) typeExpr).getEmbeddedType(environment);
        if (elementType != null) {
          resultType = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(elementType)).withPosition(this);
        }
      }
    }
    return resultType;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (lookupStyle == IndexLookupStyle.ExpressionLookupMethod) {
      expression.writeJava(sb, environment);
      sb.append(".lookup(");
      arg.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(")");
    } else if (lookupStyle == IndexLookupStyle.ExpressionGetOrCreateMethod) {
      expression.writeJava(sb, environment);
      sb.append(".getOrCreate(");
      if (castArg != null) {
        sb.append("(").append(castArg).append(") ");
      }
      arg.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(")");
    } else if (lookupStyle == IndexLookupStyle.UtilityFunction) {
      sb.append("Utility.lookup(");
      expression.writeJava(sb, environment);
      sb.append(", ");
      arg.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(")");
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
    arg.free(environment);
  }
}
