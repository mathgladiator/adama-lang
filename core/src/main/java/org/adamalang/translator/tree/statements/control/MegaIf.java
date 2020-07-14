/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.statements.control;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

/** classical if statement with else if and else */
public class MegaIf extends Statement {
  public static class Condition extends DocumentPosition {
    public final Token asToken;
    public final Token closeParen;
    private TyType elementType;
    public final Expression expression;
    private String generatedVariable;
    private TyType maybeType;
    public final String name;
    public final Token nameToken;
    public final Token openParen;

    public Condition(final Token openParen, final Expression expression, final Token asToken, final Token nameToken, final Token closeParen) {
      this.openParen = openParen;
      this.expression = expression;
      this.asToken = asToken;
      this.nameToken = nameToken;
      if (asToken != null) {
        name = nameToken.text;
      } else {
        name = null;
      }
      this.closeParen = closeParen;
    }

    public void emit(final Consumer<Token> yielder) {
      yielder.accept(openParen);
      expression.emit(yielder);
      if (asToken != null) {
        yielder.accept(asToken);
        yielder.accept(nameToken);
      }
      yielder.accept(closeParen);
    }
  }
  public static class If {
    public final Block code;
    public final Condition condition;
    public final Token elseToken;
    public final Token ifToken;

    public If(final Token elseToken, final Token ifToken, final Condition condition, final Block code) {
      this.elseToken = elseToken;
      this.ifToken = ifToken;
      this.condition = condition;
      this.code = code;
    }

    public void emit(final Consumer<Token> yielder) {
      if (elseToken != null) {
        yielder.accept(elseToken);
      }
      if (ifToken != null) {
        yielder.accept(ifToken);
      }
      condition.emit(yielder);
      code.emit(yielder);
    }
  }

  public final ArrayList<If> branches;
  public Block elseBranch;
  public Token elseToken;

  public MegaIf(final Token ifToken, final Condition condition, final Block code) {
    branches = new ArrayList<>();
    branches.add(new If(ifToken, null, condition, code));
    ingest(ifToken);
    ingest(condition);
    ingest(code);
  }

  /** add an 'else if' branch */
  public void add(final Token elseToken, final Token ifToken, final Condition condition, final Block code) {
    branches.add(new If(elseToken, ifToken, condition, code));
    ingest(condition);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    for (final If branch : branches) {
      branch.emit(yielder);
    }
    if (elseToken != null) {
      yielder.accept(elseToken);
      elseBranch.emit(yielder);
    }
  }

  /** add the else branch */
  public void setElse(final Token elseToken, final Block elseBranch) {
    this.elseToken = elseToken;
    this.elseBranch = elseBranch;
    ingest(elseBranch);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    var flow = ControlFlow.Returns;
    for (final If branch : branches) {
      if (branch.condition.name != null) {
        final var compute = environment.scopeWithComputeContext(ComputeContext.Computation);
        branch.condition.maybeType = branch.condition.expression.typing(compute, null /* no suggestion */);
        if (environment.rules.IsMaybe(branch.condition.maybeType, false)) {
          branch.condition.elementType = ((DetailContainsAnEmbeddedType) branch.condition.maybeType).getEmbeddedType(compute);
          if (branch.condition.elementType != null) {
            compute.define(branch.condition.name, branch.condition.elementType, false, branch.condition.elementType);
          }
        } else {
          branch.condition.maybeType = null;
        }
        if (branch.code.typing(compute) == ControlFlow.Open) {
          flow = ControlFlow.Open;
        }
      } else {
        final var expressionType = branch.condition.expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null /* nope */);
        environment.rules.IsBoolean(expressionType, false);
        if (branch.code.typing(environment) == ControlFlow.Open) {
          flow = ControlFlow.Open;
        }
      }
    }
    if (elseBranch != null) {
      if (elseBranch.typing(environment) == ControlFlow.Open) {
        flow = ControlFlow.Open;
      }
    } else {
      flow = ControlFlow.Open;
    }
    return flow;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    var first = true;
    for (final If branch : branches) {
      if (branch.condition.name != null && branch.condition.maybeType != null) {
        branch.condition.generatedVariable = "_AutoCondition" + branch.condition.name + "_" + environment.autoVariable();
        sb.append(branch.condition.maybeType.getJavaConcreteType(environment)).append(" ").append(branch.condition.generatedVariable).append(";").writeNewline();
      }
    }
    for (final If branch : branches) {
      if (first) {
        sb.append("if (");
        first = false;
      } else {
        sb.append(" else if (");
      }
      if (branch.condition.name != null && branch.condition.maybeType != null) {
        sb.append("(").append(branch.condition.generatedVariable).append(" = ");
        branch.condition.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(").has()) {").tabUp().writeNewline();
        sb.append(branch.condition.elementType.getJavaConcreteType(environment)).append(" ").append(branch.condition.name).append(" = ").append(branch.condition.generatedVariable).append(".get();").writeNewline();
        final var next = environment.scope();
        next.define(branch.condition.name, branch.condition.elementType, false, branch.condition.elementType);
        branch.code.specialWriteJava(sb, next, false, true);
        sb.append("}");
      } else {
        branch.condition.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(") ");
        branch.code.writeJava(sb, environment);
      }
    }
    if (elseBranch != null) {
      sb.append(" else ");
      elseBranch.writeJava(sb, environment);
    }
  }
}
