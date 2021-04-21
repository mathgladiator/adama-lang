/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeAsset;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

/** defines an event for when connected or not */
public class DefineDocumentEvent extends Definition {
  public final Token clientVarToken;
  public final Token commaToken;
  public final Token parameterNameToken;
  public final Token closeParen;
  public final Block code;
  public final Token eventToken;
  public final Token openParen;
  public final DocumentEvent which;

  public DefineDocumentEvent(final Token eventToken, final DocumentEvent which, final Token openParen, final Token clientVarToken, final Token commaToken, final Token parameterNameToken, final Token closeParen, final Block code) {
    this.eventToken = eventToken;
    this.which = which;
    this.openParen = openParen;
    this.clientVarToken = clientVarToken;
    this.commaToken = commaToken;
    this.parameterNameToken = parameterNameToken;
    this.closeParen = closeParen;
    this.code = code;
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(eventToken);
    yielder.accept(openParen);
    yielder.accept(clientVarToken);
    if (commaToken != null) {
      yielder.accept(commaToken);
      yielder.accept(parameterNameToken);
    }
    yielder.accept(closeParen);
    code.emit(yielder);
  }

  public Environment nextEnvironment(final Environment environment) {
    boolean readonly = which == DocumentEvent.AskAssetAttachment;
    final var next = readonly ? environment.scopeAsReadOnlyBoundary() : environment.scope();
    if (which == DocumentEvent.ClientConnected || which == DocumentEvent.AskAssetAttachment) {
      next.setReturnType(new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, clientVarToken).withPosition(this));
    }
    if (which == DocumentEvent.AssetAttachment) {
      next.define(parameterNameToken.text, new TyNativeAsset(TypeBehavior.ReadOnlyNativeValue, null, clientVarToken).withPosition(this), true, this);
    }
    next.define(clientVarToken.text, new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, clientVarToken).withPosition(this), true, this);
    return next;
  }

  @Override
  public void typing(final Environment environment) {
    ControlFlow codeControlFlow = code.typing(nextEnvironment(environment));
    if (which == DocumentEvent.ClientConnected && codeControlFlow == ControlFlow.Open) {
      environment.document.createError(this, String.format("The @connected handler must return a boolean"), "DocumentEvents");
    }
    if (which == DocumentEvent.AskAssetAttachment && codeControlFlow == ControlFlow.Open) {
      environment.document.createError(this, String.format("The @can_attach handler must return a boolean"), "DocumentEvents");
    }
  }
}
