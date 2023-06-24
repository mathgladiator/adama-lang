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
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.TreeMap;
import java.util.function.Consumer;

/** defines a URI to get a web resource */
public class DefineWebDelete extends Definition implements UriAction {

  public final Token webToken;
  public final Token deleteToken;
  public final Uri uri;
  public final Block code;

  public DefineWebDelete(Token webToken, Token deleteToken, Uri uri, Block code) {
    this.webToken = webToken;
    this.deleteToken = deleteToken;
    this.uri = uri;
    this.code = code;
    ingest(webToken);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(webToken);
    yielder.accept(deleteToken);
    uri.emit(yielder);
    code.emit(yielder);
  }

  @Override
  public TreeMap<String, TyType> parameters() {
    return uri.variables;
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsReadOnlyBoundary().scopeAsWeb("delete");
    uri.extendInto(env);
    uri.typing(env);
    return env;
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (environment) -> {
      Environment env = next(environment);
      if (code.typing(env) == ControlFlow.Open) {
        environment.document.createError(this, String.format("The @web handlers must return a message"), "Web");
      }
    });
  }
}
