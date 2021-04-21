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

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

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
}
