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
package org.adamalang.translator.tree.statements;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

import java.util.function.Consumer;

/** ; */
public class EmptyStatement extends Statement {
  public final Token emptyStatementToken;

  public EmptyStatement(final Token emptyStatementToken) {
    this.emptyStatementToken = emptyStatementToken;
    ingest(emptyStatementToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(emptyStatementToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append(";").writeNewline();
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
