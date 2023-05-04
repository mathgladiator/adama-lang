/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions.config;

import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.DocumentEvent;
import org.adamalang.translator.tree.definitions.config.StaticPiece;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.TypeCheckerProxy;
import org.adamalang.translator.tree.types.natives.TyInternalReadonlyClass;
import org.adamalang.translator.tree.types.natives.TyNativeAsset;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;

import java.util.function.Consumer;

/** defines an event for when connected or not */
public class DefineDocumentEvent extends StaticPiece {
  public final Token parameterNameToken;
  public final Token closeParen;
  public final Block code;
  public final Token eventToken;
  public final Token openParen;
  public final DocumentEvent which;
  public String contextVariable;

  public DefineDocumentEvent(final Token eventToken, final DocumentEvent which, final Token openParen, final Token parameterNameToken, final Token closeParen, final Block code) {
    this.eventToken = eventToken;
    this.which = which;
    this.openParen = openParen;
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
      yielder.accept(parameterNameToken);
      yielder.accept(closeParen);
    }
    code.emit(yielder);
  }

  @Override
  public void typing(final Environment environment) {
    internalTyping(environment);
  }

  public void typing(TypeCheckerProxy checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    if (parameterNameToken != null) {
      fe.define(parameterNameToken.text);
    }
    code.free(fe);
    checker.register(fe.free, (env) -> internalTyping(env.scopeAsPolicy()));
  }

  public void internalTyping(final Environment environment) {
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
        case Delete:
          environment.document.createError(this, String.format("The @delete handler must return a boolean"), "DocumentEvents");
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
      return next;
    }
    boolean readonly = which == DocumentEvent.AskAssetAttachment;
    final var next = readonly ? environment.scopeAsReadOnlyBoundary().scopeAsDocumentEvent() : environment.scopeAsDocumentEvent();
    if ("boolean".equals(which.returnType)) {
      next.setReturnType(new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, eventToken).withPosition(this));
    }
    if (which == DocumentEvent.AssetAttachment && parameterNameToken != null) {
      next.define(parameterNameToken.text, new TyNativeAsset(TypeBehavior.ReadOnlyNativeValue, null, parameterNameToken).withPosition(this), true, this);
    }
    return next;
  }
}
