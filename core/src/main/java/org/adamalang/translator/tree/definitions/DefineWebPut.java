/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.web.Uri;
import org.adamalang.translator.tree.definitions.web.UriAction;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyNativeRef;

import java.util.TreeMap;
import java.util.function.Consumer;

public class DefineWebPut extends Definition implements UriAction {
  public final Token webToken;
  public final Token postToken;
  public final Uri uri;
  public final Token openParen;
  public final Token messageType;
  public final Token messageVariable;
  public final Token closeParen;
  public final Block code;
  private TyType messageTypeFound;

  public DefineWebPut(Token webToken, Token postToken, Uri uri, Token openParen, Token messageType, Token messageVariable, Token closeParen, Block code) {
    this.webToken = webToken;
    this.postToken = postToken;
    this.uri = uri;
    this.openParen = openParen;
    this.messageType = messageType;
    this.messageVariable = messageVariable;
    this.closeParen = closeParen;
    this.code = code;
    this.messageTypeFound = null;
    ingest(webToken);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(webToken);
    yielder.accept(postToken);
    uri.emit(yielder);
    yielder.accept(openParen);
    yielder.accept(messageType);
    yielder.accept(messageVariable);
    yielder.accept(closeParen);
    code.emit(yielder);
  }

  @Override
  public TreeMap<String, TyType> parameters() {
    return uri.variables;
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsAbortable().scopeAsWeb("put");
    uri.extendInto(env);
    env.define(messageVariable.text, messageTypeFound, false, this);
    uri.typing(env);
    return env.scopeWithCache("__currentWebCache");
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (environment) -> {
      messageTypeFound = environment.rules.Resolve(new TyNativeRef(TypeBehavior.ReadWriteNative, null, messageType), false);
      if (messageTypeFound != null) {
        if (environment.rules.IsNativeMessage(messageTypeFound, false)) {
          Environment env = next(environment);
          if (code.typing(env) == ControlFlow.Open) {
            environment.document.createError(this, String.format("The @web handlers must return a message"));
          }
        }
      }
    });
  }
}
