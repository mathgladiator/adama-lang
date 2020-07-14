/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;

/** defines a test to run on an empty document, this helps validate flow */
public class DefineTest extends Definition {
  public final Block code;
  public final String name;
  public final Token nameToken;
  public final Token testToken;

  public DefineTest(final Token testToken, final Token nameToken, final Block code) {
    this.testToken = testToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.code = code;
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(testToken);
    yielder.accept(nameToken);
    code.emit(yielder);
  }

  @Override
  public void typing(final Environment environment) {
    code.typing(environment.scopeAsUnitTest());
  }
}
