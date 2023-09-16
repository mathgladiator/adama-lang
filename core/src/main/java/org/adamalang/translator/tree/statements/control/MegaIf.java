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
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

import java.util.ArrayList;
import java.util.function.Consumer;

/** classical if statement with else if and else */
public class MegaIf extends Statement {
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
        if (branch.code.typing(compute.scope()) == ControlFlow.Open) {
          flow = ControlFlow.Open;
        }
      } else {
        final var expressionType = branch.condition.expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null /* nope */);
        if (environment.rules.IsMaybe(expressionType, true)) {
          final var compute = environment.scopeWithComputeContext(ComputeContext.Computation);
          final var subExpressionType = ((DetailContainsAnEmbeddedType) expressionType).getEmbeddedType(compute);
          environment.rules.IsBoolean(subExpressionType, false);
          branch.condition.extractBooleanMaybe = true;
        } else {
          environment.rules.IsBoolean(expressionType, false);
        }
        if (branch.code.typing(environment.scope()) == ControlFlow.Open) {
          flow = ControlFlow.Open;
        }
      }
    }
    if (elseBranch != null) {
      if (elseBranch.typing(environment.scope()) == ControlFlow.Open) {
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
      } else if (branch.condition.extractBooleanMaybe) {
        sb.append("LibMath.isTrue(");
        branch.condition.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(")) ");
        branch.code.writeJava(sb, environment.scope());
      } else {
        branch.condition.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(") ");
        branch.code.writeJava(sb, environment.scope());
      }
    }
    if (elseBranch != null) {
      sb.append(" else ");
      elseBranch.writeJava(sb, environment.scope());
    }
  }

  /** add the else branch */
  public void setElse(final Token elseToken, final Block elseBranch) {
    this.elseToken = elseToken;
    this.elseBranch = elseBranch;
    ingest(elseBranch);
  }

  public static class Condition extends DocumentPosition {
    public final Token asToken;
    public final Token closeParen;
    public final Expression expression;
    public final String name;
    public final Token nameToken;
    public final Token openParen;
    private TyType elementType;
    private String generatedVariable;
    private TyType maybeType;
    public boolean extractBooleanMaybe;

    public Condition(final Token openParen, final Expression expression, final Token asToken, final Token nameToken, final Token closeParen) {
      this.openParen = openParen;
      this.expression = expression;
      this.asToken = asToken;
      this.nameToken = nameToken;
      if (nameToken != null) {
        name = nameToken.text;
      } else {
        name = null;
      }
      this.closeParen = closeParen;
      this.maybeType = null;
      this.extractBooleanMaybe = false;
      ingest(openParen);
      ingest(closeParen);
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

  public static class If extends DocumentPosition {
    public final Block code;
    public final Condition condition;
    public final Token elseToken;
    public final Token ifToken;

    public If(final Token elseToken, final Token ifToken, final Condition condition, final Block code) {
      this.elseToken = elseToken;
      this.ifToken = ifToken;
      this.condition = condition;
      this.code = code;
      if (elseToken != null) {
        ingest(elseToken);
      }
      if (ifToken != null) {
        ingest(ifToken);
      }
      ingest(code);
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

  @Override
  public void free(FreeEnvironment environment) {
    for (final If branch : branches) {
      branch.condition.expression.free(environment);
      branch.code.free(environment.push());
    }
    if (elseBranch != null) {
      elseBranch.free(environment.push());
    }
  }
}
