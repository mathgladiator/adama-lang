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
package org.adamalang.translator.tree.statements.loops;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

/** classical while(condition) {...} loop */
public class While extends Statement {
  public final Block code;
  public final Expression condition;
  public final Token endParen;
  public final Token openParen;
  public final Token whileToken;

  public While(final Token whileToken, final Token openParen, final Expression condition, final Token endParen, final Block code) {
    this.whileToken = whileToken;
    ingest(whileToken);
    this.openParen = openParen;
    this.condition = condition;
    this.endParen = endParen;
    this.code = code;
    ingest(condition);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(whileToken);
    yielder.accept(openParen);
    condition.emit(yielder);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    condition.format(formatter);
    code.format(formatter);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var conditionType = condition.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsBoolean(conditionType, false);
    code.typing(environment.scope());
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.state.isStatic()) {
      sb.append("while (__static_state.__goodwill(").append(condition.toArgs(true)).append(") && (");
    } else {
      sb.append("while (__goodwill(").append(condition.toArgs(true)).append(") && (");
    }
    condition.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append(")) ");
    code.writeJava(sb, environment.scope());
  }

  @Override
  public void free(FreeEnvironment environment) {
    condition.free(environment);
    code.free(environment.push());
  }
}
