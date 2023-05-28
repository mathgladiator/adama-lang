package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.EnumConstant;
import org.adamalang.translator.tree.expressions.constants.IntegerConstant;
import org.adamalang.translator.tree.expressions.constants.StringConstant;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

public class Case extends Statement {
  public final Token token;
  public final Expression value;
  public final Token colon;

  public Case(Token token, Expression value, Token colon) {
    this.token = token;
    this.value = value;
    this.colon = colon;
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
    value.typing(environment, null);

    return ControlFlow.Open;
  }

  @Override
  public void free(FreeEnvironment environment) {
    // nothing
  }

  @Override
  public void writeJava(StringBuilderWithTabs sb, Environment environment) {
    sb.append("case ");
    value.writeJava(sb, environment);
    sb.append(":").writeNewline();
  }
}
