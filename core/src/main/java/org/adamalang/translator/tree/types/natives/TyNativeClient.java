/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.NoOneClientConstant;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailEqualityTestingRequiresWrapping;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

public class TyNativeClient extends TySimpleNative implements DetailHasBridge, //
    DetailEqualityTestingRequiresWrapping, //
    AssignmentViaNative //
{
  public final Token token;

  public TyNativeClient(final Token token) {
    super("NtClient", "NtClient");
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "client";
  }

  @Override
  public String getBridge(final Environment environment) {
    return "NativeBridge.CLIENT_NATIVE_SUPPORT";
  }

  @Override
  public String getEqualityTestingBinaryPattern() {
    return "(%s.equals(%s))";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new NoOneClientConstant(Token.WRAP("@no_one")).withPosition(forWhatExpression);
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position) {
    return new TyNativeClient(token).withPosition(position);
  }
}
