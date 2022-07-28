/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyInternalReadonlyClass;
import org.adamalang.translator.tree.types.natives.TyNativeAsset;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;

import java.util.function.Consumer;

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
  public String contextVariable;

  public DefineDocumentEvent(final Token eventToken, final DocumentEvent which, final Token openParen, final Token clientVarToken, final Token commaToken, final Token parameterNameToken, final Token closeParen, final Block code) {
    this.eventToken = eventToken;
    this.which = which;
    this.openParen = openParen;
    this.clientVarToken = clientVarToken;
    this.commaToken = commaToken;
    this.parameterNameToken = parameterNameToken;
    this.closeParen = closeParen;
    this.code = code;
    this.contextVariable = null;
    ingest(code);
  }

  public void setContextVariable(String contextVariable) {
    this.contextVariable = contextVariable;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(eventToken);
    if (openParen != null) {
      yielder.accept(openParen);
      if (which == DocumentEvent.AssetAttachment && clientVarToken == null) {
        yielder.accept(parameterNameToken);
      } else {
        yielder.accept(clientVarToken);
        if (commaToken != null) {
          yielder.accept(commaToken);
          yielder.accept(parameterNameToken);
        }
      }
      yielder.accept(closeParen);
    }
    code.emit(yielder);
  }

  @Override
  public void typing(final Environment environment) {
    switch (which) {
      case AssetAttachment: {
        if (parameterNameToken == null) {
          environment.document.createError(this, String.format("The @attached a parameter (i.a. @attached(asset) {...})"), "DocumentEvents");
        }
      }
    }
    ControlFlow codeControlFlow = code.typing(nextEnvironment(environment));
    if (codeControlFlow == ControlFlow.Open) {
      switch (which) {
        case ClientConnected:
          environment.document.createError(this, String.format("The @connected handler must return a boolean"), "DocumentEvents");
          return;
        case AskCreation:
          environment.document.createError(this, String.format("The 'create' policy must return a boolean"), "DocumentEvents");
          return;
        case AskInvention:
          environment.document.createError(this, String.format("The 'invent' policy must return a boolean"), "DocumentEvents");
          return;
        case AskSendWhileDisconnected:
          environment.document.createError(this, String.format("The 'send' policy must return a boolean"), "DocumentEvents");
          return;
        case AskAssetAttachment:
          environment.document.createError(this, String.format("The @can_attach handler must return a boolean"), "DocumentEvents");
          return;
      }
    }
  }

  public Environment nextEnvironment(final Environment environment) {
    if (which == DocumentEvent.AskCreation || which == DocumentEvent.AskInvention || which == DocumentEvent.AskSendWhileDisconnected) {
      Environment next = environment.staticPolicy().scopeStatic();
      next.setReturnType(new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, eventToken));
      if (contextVariable != null) {
        next.define(contextVariable, new TyInternalReadonlyClass(CoreRequestContext.class), true, this);
      }
      if (clientVarToken != null) {
        next.define(clientVarToken.text, new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, clientVarToken).withPosition(this), true, this);
      }
      return next;
    }
    boolean readonly = which == DocumentEvent.AskAssetAttachment;
    final var next = readonly ? environment.scopeAsReadOnlyBoundary() : environment.scope();
    if (which == DocumentEvent.ClientConnected || which == DocumentEvent.AskAssetAttachment) {
      next.setReturnType(new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, clientVarToken).withPosition(this));
    }
    if (which == DocumentEvent.AssetAttachment && parameterNameToken != null) {
      next.define(parameterNameToken.text, new TyNativeAsset(TypeBehavior.ReadOnlyNativeValue, null, parameterNameToken).withPosition(this), true, this);
    }
    if (clientVarToken != null) {
      next.define(clientVarToken.text, new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, clientVarToken).withPosition(this), true, this);
    }
    return next;
  }
}
