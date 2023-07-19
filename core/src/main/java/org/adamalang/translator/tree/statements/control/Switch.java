/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetEnums;

import java.util.function.Consumer;

public class Switch extends Statement {
  public final Token token;
  public final Token openParen;
  public final Expression expression;
  public final Token closeParen;
  public final Block code;
  public TyType caseType;

  public Switch(Token token, Token openParen, Expression expression, Token closeParen, Block code) {
    this.token = token;
    this.openParen = openParen;
    this.expression = expression;
    this.closeParen = closeParen;
    this.code = code;
    ingest(token);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(token);
    yielder.accept(openParen);
    expression.emit(yielder);
    yielder.accept(closeParen);
    code.emit(yielder);
  }

  @Override
  public ControlFlow typing(Environment environment) {
    Environment next = environment.scope();
    caseType = expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    boolean good = environment.rules.IsInteger(caseType, true) || environment.rules.IsString(caseType, true) || RuleSetEnums.IsEnum(environment, caseType, true);
    if (!good) {
      environment.document.createError(this, String.format("switch statements work with integer, string, or enum types"));
    }
    next.setCaseType(caseType);
    return code.typing(next);
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
    code.free(environment);
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    sb.append("switch (");
    expression.writeJava(sb, environment);
    sb.append(") ");
    TyType priorCaseType = environment.getCaseType();
    environment.setCaseType(caseType);
    code.writeJava(sb, environment);
    environment.setCaseType(priorCaseType);
  }
}
