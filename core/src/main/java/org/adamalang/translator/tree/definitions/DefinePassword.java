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
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/** a very special way for a user of a document to set their password */
public class DefinePassword extends Definition {
  public final Token passwordToken;
  public final Token openParen;
  public final Token passwordVar;
  public final Token endParen;
  public final Block code;

  public DefinePassword(Token passwordToken, Token openParen, Token passwordVar, Token endParen, Block code) {
    this.passwordToken = passwordToken;
    this.openParen = openParen;
    this.passwordVar = passwordVar;
    this.endParen = endParen;
    this.code = code;
    ingest(passwordToken);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(passwordToken);
    yielder.accept(openParen);
    yielder.accept(passwordVar);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsPolicy();
    TyNativeString tyStr = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, passwordToken);
    env.define(passwordVar.text, tyStr, true, this);
    return env;
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (env) -> {
      code.typing(next(env));
    });
  }
}
