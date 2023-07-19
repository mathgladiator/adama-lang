/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.statements.testing;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

/** Force a behavior of the document (like making progress) */
public class Force extends Statement {
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
      environment.document.createError(this, String.format("Forcing a step designed exclusively for testing"));
    }
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (action == Action.Step) {
      sb.append("__test_progress();");
    }
  }

  public enum Action {
    Step
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
