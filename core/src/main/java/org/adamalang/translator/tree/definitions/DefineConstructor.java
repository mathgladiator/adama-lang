/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

/** defines a constructor which runs when the document is created */
public class DefineConstructor extends Definition {
  public TyNativeClient clientType;
  public final Token clientTypeToken;
  public final Token clientVarToken;
  public final Block code;
  public final Token commaToken;
  public final Token constructToken;
  public final Token endParenToken;
  public final Token messageNameToken;
  public final Token messageTypeToken;
  public final Token openParenToken;

  public DefineConstructor(final Token constructToken, final Token openParenToken, final Token clientTypeToken, final Token clientVarToken, final Token commaToken, final Token messageTypeToken, final Token messageNameToken,
      final Token endParenToken, final Block code) {
    this.constructToken = constructToken;
    this.openParenToken = openParenToken;
    this.clientTypeToken = clientTypeToken;
    this.clientVarToken = clientVarToken;
    if (clientTypeToken != null) {
      clientType = new TyNativeClient(clientTypeToken);
      clientType.ingest(clientTypeToken);
      clientType.ingest(clientVarToken);
    }
    this.commaToken = commaToken;
    this.messageTypeToken = messageTypeToken;
    this.messageNameToken = messageNameToken;
    this.endParenToken = endParenToken;
    this.code = code;
    ingest(constructToken);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(constructToken);
    if (commaToken != null || clientTypeToken != null) {
      yielder.accept(openParenToken);
      if (clientTypeToken != null) {
        yielder.accept(clientTypeToken);
        yielder.accept(clientVarToken);
      }
      if (commaToken != null) {
        yielder.accept(commaToken);
        yielder.accept(messageTypeToken);
        yielder.accept(messageNameToken);
      }
      yielder.accept(endParenToken);
    } else if (messageNameToken != null) {
      yielder.accept(openParenToken);
      yielder.accept(messageTypeToken);
      yielder.accept(messageNameToken);
      yielder.accept(endParenToken);
    }
    code.emit(yielder);
  }

  @Override
  public void typing(final Environment environment) {
    final var next = environment.scope();
    if (clientType != null) {
      next.define(clientVarToken.text, clientType, true, clientType);
    }
    if (messageTypeToken != null) {
      final var messageType = next.rules.FindMessageStructure(messageTypeToken.text, new DocumentPosition(), false);
      if (messageType != null) {
        next.define(messageNameToken.text, messageType, false, messageType);
      }
    }
    next.setReturnType(null);
    if (code != null) {
      code.typing(next);
    }
  }
}
