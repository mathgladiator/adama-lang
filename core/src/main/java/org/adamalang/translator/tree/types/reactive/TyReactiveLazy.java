/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.reactive;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;

public class TyReactiveLazy extends TyType implements DetailContainsAnEmbeddedType, DetailComputeRequiresGet // to get the native value
{
  public final TyType computedType;

  public TyReactiveLazy(final TyType computedType) {
    super(TypeBehavior.ReadOnlyGetNativeValue);
    this.computedType = computedType;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAdamaType() {
    return "auto:" + computedType.getAdamaType();
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return computedType;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("RxLazy<%s>", computedType.getJavaBoxType(environment));
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyReactiveLazy(computedType).withPosition(position);
  }

  @Override
  public TyType typeAfterGet(final Environment environment) {
    return getEmbeddedType(environment);
  }

  @Override
  public void typing(final Environment environment) {
    computedType.typing(environment);
    var typedAs = environment.rules.Resolve(computedType, false);
    if (!(typedAs instanceof DetailHasDeltaType)) {
      environment.document.createError(this, String.format("Lazy type has inappropriate type `%s`", computedType.getAdamaType()), "Lazy");
      return;
    }
    while (typedAs instanceof DetailContainsAnEmbeddedType) {
      typedAs = environment.rules.Resolve(((DetailContainsAnEmbeddedType) typedAs).getEmbeddedType(environment), false);
      if (!(typedAs instanceof DetailHasDeltaType)) {
        environment.document.createError(this, String.format("Lazy type has inappropriate type `%s`", computedType.getAdamaType()), "Lazy");
        return;
      }
    }
  }
}
