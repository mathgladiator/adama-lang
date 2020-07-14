/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

/** defines an event for when connected or not */
public class DefineDocumentEvent extends Definition {
  public final Token clientVarToken;
  public final Token closeParen;
  public final Block code;
  private ControlFlow codeControlFlow;
  public final Token eventToken;
  public final Token openParen;
  public final DocumentEvent which;

  public DefineDocumentEvent(final Token eventToken, final DocumentEvent which, final Token openParen, final Token clientVarToken, final Token closeParen, final Block code) {
    this.eventToken = eventToken;
    this.which = which;
    this.openParen = openParen;
    this.clientVarToken = clientVarToken;
    this.closeParen = closeParen;
    this.code = code;
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(eventToken);
    yielder.accept(openParen);
    yielder.accept(clientVarToken);
    yielder.accept(closeParen);
    code.emit(yielder);
  }

  public Environment nextEnvironment(final Environment environment) {
    final var next = environment.scope();
    if (which == DocumentEvent.ClientConnected) {
      next.setReturnType(new TyNativeBoolean(clientVarToken).makeCopyWithNewPosition(this));
    }
    next.define(clientVarToken.text, new TyNativeClient(clientVarToken).withPosition(this), true, this);
    return next;
  }

  @Override
  public void typing(final Environment environment) {
    codeControlFlow = code.typing(nextEnvironment(environment));
    if (which == DocumentEvent.ClientConnected && codeControlFlow == ControlFlow.Open) {
      environment.document.createError(this, String.format("The @connected handler must return a boolean"), "ConnectionEvents");
    }
  }
}
