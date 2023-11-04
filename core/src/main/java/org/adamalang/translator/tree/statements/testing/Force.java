/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree.statements.testing;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
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
  public void format(Formatter formatter) {
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
