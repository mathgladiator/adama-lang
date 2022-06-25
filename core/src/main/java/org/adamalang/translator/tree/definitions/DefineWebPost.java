/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.web.Uri;
import org.adamalang.translator.tree.definitions.web.UriAction;
import org.adamalang.translator.tree.statements.Block;

import java.util.function.Consumer;

public class DefineWebPost extends Definition implements UriAction {
  public final Token webToken;
  public final Token postToken;
  public final Uri uri;
  public final Token openParen;
  public final Token messageType;
  public final Token messageVariable;
  public final Token closeParen;
  public final Block code;

  public DefineWebPost(Token webToken, Token postToken, Uri uri, Token openParen, Token messageType, Token messageVariable, Token closeParen, Block code) {
    this.webToken = webToken;
    this.postToken = postToken;
    this.uri = uri;
    this.openParen = openParen;
    this.messageType = messageType;
    this.messageVariable = messageVariable;
    this.closeParen = closeParen;
    this.code = code;
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
  public void typing(Environment environment) {
    Environment env = environment.scopeAsReadOnlyBoundary();
    uri.extendInto(env);
    uri.typing(env);
    code.typing(env);
  }
}
