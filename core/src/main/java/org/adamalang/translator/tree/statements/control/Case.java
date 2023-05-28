/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.*;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetEnums;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Case extends Statement {
  public final Token token;
  public final Expression value;
  public final Token colon;
  private ArrayList<Integer> enumArray;

  public Case(Token token, Expression value, Token colon) {
    this.token = token;
    this.value = value;
    this.colon = colon;
    this.enumArray = null;
    ingest(token);
    ingest(colon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(token);
    value.emit(yielder);
    yielder.accept(colon);
  }

  @Override
  public ControlFlow typing(Environment environment) {
    value.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    TyType caseType = environment.getCaseType();
    if (caseType == null) {
      environment.document.createError(this, String.format("case label should be within a switch statement"), "SwitchCase");
      return ControlFlow.Open;
    }
    if (environment.rules.IsInteger(caseType, true) && !(value instanceof IntegerConstant)) {
      environment.document.createError(this, String.format("case label should be an integer constant"), "SwitchCase");
    }
    if (environment.rules.IsLong(caseType, true) && !(value instanceof LongConstant || value instanceof IntegerConstant)) {
      environment.document.createError(this, String.format("case label should be an long constant"), "SwitchCase");
    }
    if (environment.rules.IsString(caseType, true) && !(value instanceof StringConstant)) {
      environment.document.createError(this, String.format("case label should be an string constant"), "SwitchCase");
    }
    if (RuleSetEnums.IsEnum(environment, caseType, true)) {
      if (value instanceof EnumConstant) {
        return ControlFlow.Open;
      } else if (value instanceof EnumValuesArray) {
        enumArray = ((EnumValuesArray) value).values(environment);
      }
    }
    return ControlFlow.Open;
  }

  @Override
  public void free(FreeEnvironment environment) {
    value.free(environment);
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    if (enumArray != null) {
      int countDown = enumArray.size();
      for (Integer val : enumArray) {
        countDown --;
        sb.append("case ").append("" + val).append(":");
        if (countDown > 0) {
          sb.writeNewline();
        }
      }
    } else {
      sb.append("case ");
      value.writeJava(sb, environment);
      sb.append(":");
    }
  }
}
