/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.statements.control;

import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

/** transition the state machine, and make sure we transact the current state */
public class TransitionStateMachine extends Statement {
  private final Expression evaluateIn;
  public final Token inToken;
  public final Expression next;
  public final Token semicolonToken;
  public final Token transitionToken;

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
}
