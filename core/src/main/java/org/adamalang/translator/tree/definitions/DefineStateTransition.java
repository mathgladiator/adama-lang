/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

/**
 * A document is in a variety of states, and as such we need code to run in each state; this is a
 * state transition that maps which code to run in a specific state.
 */
public class DefineStateTransition extends Definition {
  public final String name;
  public final Token nameToken;
  public Block code;

  public DefineStateTransition(final Token nameToken, final Block code) {
    this.nameToken = nameToken;
    name = nameToken.text.substring(1);
    this.code = code;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(nameToken);
    code.emit(yielder);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (env) -> code.typing(env.scopeAsStateMachineTransition()));
  }
}
