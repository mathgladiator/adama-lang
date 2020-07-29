/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

public class TyNativeChannel extends TyType implements DetailTypeHasMethods, DetailContainsAnEmbeddedType, AssignmentViaNative {
  public final Token channelToken;
  public final Token readonlyToken;
  public final TokenizedItem<TyType> tokenizedType;

  public TyNativeChannel(final TypeBehavior behavior, final Token readonlyToken, final Token channelToken, final TokenizedItem<TyType> tokenizedType) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.channelToken = channelToken;
    this.tokenizedType = tokenizedType;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(channelToken);
    tokenizedType.emitBefore(yielder);
    tokenizedType.item.emit(yielder);
    tokenizedType.emitAfter(yielder);
  }

  @Override
  public String getAdamaType() {
    return "channel<" + tokenizedType.item.getAdamaType() + ">";
  }

  @Override
  public TyType getEmbeddedType(final Environment environment) {
    return environment.rules.Resolve(tokenizedType.item, false);
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    final var resolved = environment.rules.Resolve(tokenizedType.item, false);
    return "NtChannel<" + resolved.getJavaBoxType(environment) + ">";
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return getJavaBoxType(environment);
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    final var resolvedChannelType = environment.rules.Resolve(tokenizedType.item, false);
    final var ct = new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, null);
    final var argTypes = new ArrayList<TyType>();
    if (environment.rules.IsNativeArray(resolvedChannelType, true)) {
      if ("fetch".equals(name)) {
        final var ft = new TyNativeFuture(TypeBehavior.ReadOnlyNativeValue, readonlyToken, null, new TokenizedItem<>(resolvedChannelType.withPosition(this)));
        argTypes.add(ct.withPosition(this));
        return new TyNativeFunctional("fetch", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("fetch", ft.withPosition(this), argTypes, false)), FunctionStyleJava.ExpressionThenArgs);
      }
      if ("choose".equals(name)) {
        final var maft = new TyNativeFuture(TypeBehavior.ReadOnlyNativeValue, readonlyToken, null,
            new TokenizedItem<>(new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(resolvedChannelType)).withPosition(this)));
        final var limit = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("int"));
        argTypes.add(ct.withPosition(this));
        argTypes.add(resolvedChannelType.withPosition(this));
        argTypes.add(limit.withPosition(this));
        return new TyNativeFunctional("choose", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("choose", maft.withPosition(this), argTypes, false)), FunctionStyleJava.ExpressionThenArgs);
      }
    } else {
      if ("fetch".equals(name)) {
        final var ft = new TyNativeFuture(TypeBehavior.ReadOnlyNativeValue, readonlyToken, null, new TokenizedItem<>(resolvedChannelType.withPosition(this)));
        argTypes.add(ct.withPosition(this));
        return new TyNativeFunctional("fetch", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("fetch", ft.withPosition(this), argTypes, false)), FunctionStyleJava.ExpressionThenArgs);
      }
      if ("decide".equals(name)) {
        final var mft = new TyNativeFuture(TypeBehavior.ReadOnlyNativeValue, readonlyToken, null,
            new TokenizedItem<>(new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(resolvedChannelType)).withPosition(this)));
        argTypes.add(ct.withPosition(this));
        argTypes.add(new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, resolvedChannelType, Token.WRAP("[]")).withPosition(this));
        return new TyNativeFunctional("decide", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("decide", mft.withPosition(this), argTypes, false)), FunctionStyleJava.ExpressionThenArgs);
      }
    }
    return null;
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeChannel(newBehavior, readonlyToken, channelToken, tokenizedType).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    tokenizedType.item.typing(environment);
  }
}
