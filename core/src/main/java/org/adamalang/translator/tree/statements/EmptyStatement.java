/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.statements;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

import java.util.function.Consumer;

/** ; */
public class EmptyStatement extends Statement {
  public final Token emptyStatementToken;

  public EmptyStatement(final Token emptyStatementToken) {
    this.emptyStatementToken = emptyStatementToken;
    ingest(emptyStatementToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(emptyStatementToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append(";").writeNewline();
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
