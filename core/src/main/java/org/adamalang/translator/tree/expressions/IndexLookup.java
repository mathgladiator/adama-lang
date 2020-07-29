/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions;

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailIndexLookup;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;

/** return a maybe type from an index lookup. The maybe forces a range check, so
 * it is always valid. */
public class IndexLookup extends Expression {
  public final Expression arg;
  public final Token bracketCloseToken;
  public final Token bracketOpenToken;
  public final Expression expression;
  private IndexLookupStyle lookupStyle;

  public IndexLookup(final Expression expression, final Token bracketOpenToken, final Expression arg, final Token bracketCloseToken) {
    this.expression = expression;
    this.bracketOpenToken = bracketOpenToken;
    this.arg = arg;
    this.bracketCloseToken = bracketCloseToken;
    this.ingest(expression);
    this.ingest(arg);
    this.ingest(bracketCloseToken);
    lookupStyle = IndexLookupStyle.Unknown;
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
      lookupStyle = IndexLookupStyle.Method;
      final var typeArg = arg.typing(environment.scopeWithComputeContext(ComputeContext.Computation), mapType.getDomainType(environment));
      if (environment.rules.CanTypeAStoreTypeB(mapType.getDomainType(environment), typeArg, StorageTweak.None, false)) {
        resultType = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(mapType.getRangeType(environment))).withPosition(this);
      }
    } else {
      environment.rules.IsIterable(typeExpr, false);
      if (typeExpr instanceof DetailIndexLookup) {
        lookupStyle = ((DetailIndexLookup) typeExpr).getLookupStyle(environment);
      }
      final var typeArg = arg.typing(environment.scopeWithComputeContext(ComputeContext.Computation), new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null));
      environment.rules.IsInteger(typeArg, false);
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
    if (lookupStyle == IndexLookupStyle.Method) {
      expression.writeJava(sb, environment);
      sb.append(".lookup(");
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
}
