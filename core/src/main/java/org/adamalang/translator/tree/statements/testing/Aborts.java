/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

public class Aborts extends Statement {
  private final Token aborts;
  private final Block code;

  public Aborts(Token aborts, Block code) {
    this.aborts = aborts;
    this.code = code;
    ingest(aborts);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(aborts);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    code.format(formatter);
  }

  @Override
  public ControlFlow typing(Environment environment) {
    if (!environment.state.isTesting()) {
      environment.document.createError(this, "@aborts is only applicable within tests");
    }
    return code.typing(environment.scopeAsAbortable());
  }

  @Override
  public void free(FreeEnvironment environment) {
    code.free(environment);
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    String variable = "__aborts_" + environment.autoVariable();
    sb.append("boolean ").append(variable).append(" = false;").writeNewline();
    sb.append("try");
    code.writeJava(sb, environment);
    sb.append(" catch (AbortMessageException __ame) {").tabUp().writeNewline();
    sb.append(variable).append(" = true;").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("__assert_truth(").append(variable).append(toArgs(false)).append(");");
  }
}
