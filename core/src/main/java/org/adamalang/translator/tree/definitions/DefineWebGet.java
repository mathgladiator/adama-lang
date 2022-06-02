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
import org.adamalang.translator.tree.statements.Block;

import java.util.function.Consumer;

/** defines a URI to get a web resource */
public class DefineWebGet extends Definition {

  public final Token webToken;
  public final Token getToken;
  public final WebUri uri;
  public final Block code;

  public DefineWebGet(Token webToken, Token getToken, WebUri uri, Block code) {
    this.webToken = webToken;
    this.getToken = getToken;
    this.uri = uri;
    this.code = code;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(webToken);
    yielder.accept(getToken);
    uri.emit(yielder);
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
