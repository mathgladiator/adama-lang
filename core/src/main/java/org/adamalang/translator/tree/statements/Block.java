/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.statements;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

/** {statements*} */
public class Block extends Statement {
  private Token closeBraceToken;
  private final Token openBraceToken;
  public final ArrayList<Statement> statements;

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

  public void end(final Token closeBraceToken) {
    this.closeBraceToken = closeBraceToken;
    ingest(closeBraceToken);
  }

  public void specialWriteJava(final StringBuilderWithTabs sb, final Environment environment, final boolean brace, final boolean tabDownOnEnd) {
    final var child = environment.scope();
    final var n = statements.size();
    if (n == 0 && brace) {
      sb.append("{}");
      return;
    }
    if (brace) {
      sb.tabUp().append("{").writeNewline();
      if (!environment.state.hasNoCost()) {
        sb.append(String.format("__code_cost += %d;", 1 + statements.size())).writeNewline();
      }
    } else if (!environment.state.hasNoCost()) {
      sb.append(String.format("__code_cost += %d;", 1 + statements.size())).writeNewline();
    }
    for (var k = 0; k < n; k++) {
      final var s = statements.get(k);
      final var codeCoverageIndex = environment.codeCoverageTracker.register(s);
      if (environment.state.options.produceCodeCoverage) {
        sb.append(String.format("__track(%d);", codeCoverageIndex)).writeNewline();
      }
      s.writeJava(sb, child);
      if (k == n - 1 && (brace || tabDownOnEnd)) {
        sb.tabDown();
      }
      sb.writeNewline();
    }
    if (brace) {
      sb.append("}");
    }
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    var flow = ControlFlow.Open;
    for (final Statement stmt : statements) {
      // check that it must be Open
      if (flow == ControlFlow.Returns) {
        environment.document.createError(stmt, String.format("This code is unreachable."), "Block");
      }
      if (stmt.typing(environment) == ControlFlow.Returns) {
        flow = ControlFlow.Returns;
      }
    }
    return flow;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    specialWriteJava(sb, environment, true, true);
  }
}
