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
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.control.AlterControlFlow;
import org.adamalang.translator.tree.statements.control.AlterControlFlowMode;
import org.adamalang.translator.tree.statements.control.Case;
import org.adamalang.translator.tree.statements.control.DefaultCase;

import java.util.ArrayList;
import java.util.function.Consumer;

/** {statements*} */
public class Block extends Statement {
  public final ArrayList<Statement> statements;
  private final Token openBraceToken;
  private Token closeBraceToken;

  public Block(final Token openBraceToken) {
    this.openBraceToken = openBraceToken;
    statements = new ArrayList<>();
    if (openBraceToken != null) {
      ingest(openBraceToken);
    }
  }

  public void add(final Statement statement) {
    if (statement != null) {
      statements.add(statement);
      ingest(statement);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (openBraceToken != null) {
      yielder.accept(openBraceToken);
    }
    for (final Statement statement : statements) {
      statement.emit(yielder);
    }
    if (closeBraceToken != null) {
      yielder.accept(closeBraceToken);
    }
  }

  @Override
  public void format(Formatter formatter) {
    if (openBraceToken != null) {
      formatter.endLine(openBraceToken);
      formatter.tabUp();
    }
    for (final Statement statement : statements) {
      Formatter.FirstAndLastToken fal = new Formatter.FirstAndLastToken();
      statement.emit(fal);
      if (fal.first != null) {
        formatter.startLine(fal.first);
        formatter.endLine(fal.last);
      }
      statement.format(formatter);
    }
    if (closeBraceToken != null) {
      formatter.tabDown();
      formatter.startLine(closeBraceToken);
      formatter.endLine(closeBraceToken);
    }
  }

  private boolean isBreak(Statement stmt ) {
    if (stmt instanceof AlterControlFlow) {
      return ((AlterControlFlow) stmt).how == AlterControlFlowMode.Break;
    }
    return false;
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    boolean hasCases = environment.getCaseType() != null;
    if (hasCases) {
      int casesThatBreak = 0;
      int casesThatReturn = 0;
      boolean inDefault = false;
      boolean hasDefault = false;
      boolean defaultReturns = false;
      boolean detectDeadCode = false;
      for (final Statement stmt : statements) {
        if (stmt instanceof Case || stmt instanceof DefaultCase) {
          inDefault = stmt instanceof DefaultCase;
          if (inDefault) {
            if (hasDefault) {
              environment.document.createError(stmt, String.format("Switch has too many defaults."));
            }
            hasDefault = true;
          }
          detectDeadCode = false;
        }
        if (detectDeadCode) {
          environment.document.createError(stmt, String.format("This code is unreachable."));
        }
        ControlFlow flow = stmt.typing(environment);
        if (flow == ControlFlow.Returns) {
          casesThatReturn++;
          if (inDefault) {
            defaultReturns = true;
          }
          detectDeadCode = true;
        }
        if (isBreak(stmt)) {
          casesThatBreak++;
        }
      }
      /*
       * if any case breaks, then it is open
       * if there are cases that return, then we really need a default that returns to indicate a returning control flow
       */
      if (casesThatBreak == 0 && casesThatReturn > 0 && hasDefault && defaultReturns) {
        return ControlFlow.Returns;
      } else {
        return ControlFlow.Open;
      }
    } else {
      var flow = ControlFlow.Open;
      for (final Statement stmt : statements) {
        // check that it must be Open
        if (flow == ControlFlow.Returns) {
          environment.document.createError(stmt, String.format("This code is unreachable."));
        }
        if (stmt.typing(environment) == ControlFlow.Returns) {
          flow = ControlFlow.Returns;
        }
      }
      return flow;
    }
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    specialWriteJava(sb, environment, true, true);
  }

  public void specialWriteJava(final StringBuilderWithTabs sb, final Environment environment, final boolean brace, final boolean tabDownOnEnd) {
    boolean hasCases = environment.getCaseType() != null;
    final var child = environment.scope();
    final var n = statements.size();
    if (n == 0 && brace) {
      sb.append("{}");
      return;
    }
    if (brace) {
      sb.tabUp().append("{").writeNewline();
      if (!environment.state.hasNoCost() && !environment.state.isStatic() && !hasCases) {
        sb.append(String.format("__code_cost += %d;", 1 + statements.size()));
        if (n == 0 && (brace || tabDownOnEnd)) {
          sb.tabDown();
        }
        sb.writeNewline();
      } else {
        if (n == 0 && (brace || tabDownOnEnd)) {
          sb.tabDown().append("/* empty */").writeNewline();
        }
      }
    } else if (!environment.state.hasNoCost() && !environment.state.isStatic() && !hasCases) {
      sb.append(String.format("__code_cost += %d;", 1 + statements.size()));
      if (n == 0 && (brace || tabDownOnEnd)) {
        sb.tabDown();
      }
      sb.writeNewline();
    } else {
      if (n == 0 && (brace || tabDownOnEnd)) {
        sb.tabDown().append("/* empty */").writeNewline();
      }
    }

    for (var k = 0; k < n; k++) {
      final var s = statements.get(k);
      final var codeCoverageIndex = environment.codeCoverageTracker.register(s);
      if (environment.state.options.produceCodeCoverage && !environment.state.isStatic() && !hasCases) {
        sb.append(String.format("__track(%d);", codeCoverageIndex)).writeNewline();
      }
      boolean tabbed = hasCases && !(s instanceof Case || s instanceof DefaultCase);
      if (tabbed) {
        sb.tab();
        sb.tabUp();
      }
      s.writeJava(sb, child);
      if (k == n - 1 && (brace || tabDownOnEnd)) {
        sb.tabDown();
      }
      if (tabbed) {
        sb.tabDown();
      }
      sb.writeNewline();
    }
    if (brace) {
      sb.append("}");
    }
  }

  public void end(final Token closeBraceToken) {
    this.closeBraceToken = closeBraceToken;
    ingest(closeBraceToken);
  }

  @Override
  public void free(FreeEnvironment environment) {
    FreeEnvironment next = environment.push();
    for (Statement stmt : statements) {
      stmt.free(next);
    }
  }
}
