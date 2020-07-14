/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyNativeReactiveRecordPtr extends TyType implements //
    AssignmentViaNative, //
    DetailTypeHasMethods, DetailContainsAnEmbeddedType //
{
  public final TyReactiveRecord source;

  public TyNativeReactiveRecordPtr(final TyReactiveRecord source) {
    this.source = source;
    ingest(source);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    source.emit(yielder);
  }

  @Override
  public String getAdamaType() {
    return source.getAdamaType();
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return source;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return source.getJavaBoxType(environment);
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return source.getJavaConcreteType(environment);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    return source.lookupMethod(name, environment);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeReactiveRecordPtr(source).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    source.typing(environment);
  }
}
