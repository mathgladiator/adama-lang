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
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

/** transition the state machine, and make sure we transact the current state */
public class TransitionStateMachine extends Statement {
  public final Token inToken;
  public final Expression next;
  public final Token semicolonToken;
  public final Token transitionToken;
  private final Expression evaluateIn;

  public TransitionStateMachine(final Token transitionToken, final Expression next, final Token inToken, final Expression evaluateIn, final Token semicolonToken) {
    this.transitionToken = transitionToken;
    ingest(transitionToken);
    this.next = next;
    ingest(next);
    this.inToken = inToken;
    this.evaluateIn = evaluateIn;
    this.semicolonToken = semicolonToken;
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(transitionToken);
    next.emit(yielder);
    if (inToken != null) {
      yielder.accept(inToken);
      evaluateIn.emit(yielder);
    }
    yielder.accept(semicolonToken);
  }

  @Override
  public void format(Formatter formatter) {
    next.format(formatter);
    if (inToken != null) {
      evaluateIn.format(formatter);
    }
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var scoped = environment.scopeWithComputeContext(ComputeContext.Computation);
    final var nextType = next.typing(scoped, null);
    scoped.rules.IsStateMachineRef(nextType, false);
    if (evaluateIn != null) {
      final var evaluateInType = evaluateIn.typing(scoped, null);
      scoped.rules.IsNumeric(evaluateInType, false);
    }
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    final var scoped = environment.scopeWithComputeContext(ComputeContext.Computation);
    sb.append("__transitionStateMachine(");
    next.writeJava(sb, scoped);
    if (evaluateIn != null) {
      sb.append(", ");
      evaluateIn.writeJava(sb, scoped);
      sb.append(");");
    } else {
      sb.append(", 0);");
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    next.free(environment);
    if (evaluateIn != null) {
      evaluateIn.free(environment);
    }
  }
}
