/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;

import java.util.function.Consumer;

public class DefaultCase extends Statement {
  public final Token token;
  public final Token colon;

  public DefaultCase(Token token, Token colon) {
    this.token = token;
    this.colon = colon;
    ingest(token);
    ingest(colon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(token);
    yielder.accept(colon);
  }

  @Override
  public ControlFlow typing(Environment environment) {

    TyType caseType = environment.getCaseType();
    if (caseType == null) {
      environment.document.createError(this, String.format("default: requires being in a switch statement"), "SwitchCase");
    }
    if (environment.checkDefaultReturnTrueIfMultiple()) {
      environment.document.createError(this, String.format("there can be only one default case"), "SwitchCase");
    }
    return ControlFlow.Open;
  }

  @Override
  public void free(FreeEnvironment environment) {
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    sb.append("default:");
  }
}
