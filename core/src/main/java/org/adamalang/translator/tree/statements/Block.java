/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.statements;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

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

  public void specialWriteJava(final StringBuilderWithTabs sb, final Environment environment, final boolean brace, final boolean tabDownOnEnd) {
    final var child = environment.scope();
    final var n = statements.size();
    if (n == 0 && brace) {
      sb.append("{}");
      return;
    }
    if (brace) {
      sb.tabUp().append("{").writeNewline();
      if (!environment.state.hasNoCost() && !environment.state.isStatic()) {
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
    } else if (!environment.state.hasNoCost() && !environment.state.isStatic()) {
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
      if (environment.state.options.produceCodeCoverage && !environment.state.isStatic()) {
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
