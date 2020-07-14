/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.BooleanConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

/** Represents the type for a boolean value (true/false); this uses the native
 * 'boolean' java type */
public class TyNativeBoolean extends TySimpleNative implements IsNativeValue, DetailHasBridge, //
    AssignmentViaNative //
{
  public final Token token;

  public TyNativeBoolean(final Token token) {
    super("boolean", "Boolean");
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "bool";
  }

  @Override
  public String getBridge(final Environment environment) {
    return "NativeBridge.BOOLEAN_NATIVE_SUPPORT";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new BooleanConstant(Token.WRAP("false"), false).withPosition(forWhatExpression);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeBoolean(token).withPosition(position);
  }
}
