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

/** classic do {} while (cond); loop */
public class DoWhile extends Statement {
  public final Token closeParen;
  public final Block code;
  public final Expression condition;
  public final Token doToken;
  public final Token endToken;
  public final Token openParen;
  public final Token whileToken;

  public DoWhile(final Token doToken, final Block code, final Token whileToken, final Token openParen, final Expression condition, final Token closeParen, final Token endToken) {
    this.doToken = doToken;
    this.code = code;
    this.whileToken = whileToken;
    this.openParen = openParen;
    this.condition = condition;
    this.closeParen = closeParen;
    this.endToken = endToken;
    ingest(doToken);
    ingest(endToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(doToken);
    code.emit(yielder);
    yielder.accept(whileToken);
    yielder.accept(openParen);
    condition.emit(yielder);
    yielder.accept(closeParen);
    yielder.accept(endToken);
  }

  @Override
  public void format(Formatter formatter) {
    code.format(formatter);
    condition.format(formatter);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var flow = code.typing(environment.scope());
    final var conditionType = condition.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsBoolean(conditionType, false);
    return flow;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("do ");
    code.writeJava(sb, environment.scope());
    if (environment.state.isStatic()) {
      sb.append(" while (__static_state.__goodwill(").append(condition.toArgs(true)).append(") && (");
    } else {
      sb.append(" while (__goodwill(").append(condition.toArgs(true)).append(") && (");
    }
    condition.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append("));");
  }

  @Override
  public void free(FreeEnvironment environment) {
    condition.free(environment);
    code.free(environment.push());
  }
}
