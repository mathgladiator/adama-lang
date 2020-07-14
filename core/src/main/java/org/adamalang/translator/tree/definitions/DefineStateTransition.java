/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;

/** A document is in a variety of states, and as such we need code to run in
 * each state; this is a state transition that maps which code to run in a
 * specific state. */
public class DefineStateTransition extends Definition {
  public Block code;
  public final String name;
  public final Token nameToken;

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

  @Override
  public void typing(final Environment environment) {
    code.typing(environment.scopeAsStateMachineTransition());
  }
}
