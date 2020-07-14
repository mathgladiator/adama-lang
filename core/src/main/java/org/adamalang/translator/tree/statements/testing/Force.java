/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.statements.testing;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

/** Force a behavior of the document (like making progress) */
public class Force extends Statement {
  public enum Action {
    Step
  }

  public final Action action;
  public final Token semicolonToken;
  public final Token token;

  public Force(final Token token, final Action action, final Token semicolonToken) {
    this.token = token;
    this.action = action;
    this.semicolonToken = semicolonToken;
    ingest(token);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
    yielder.accept(semicolonToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    if (!environment.state.isTesting()) {
      environment.document.createError(this, String.format("Forcing a step designed exclusively for testing"), "Testing");
    }
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (action == Action.Step) {
      sb.append("__test_progress();");
    }
  }
}
