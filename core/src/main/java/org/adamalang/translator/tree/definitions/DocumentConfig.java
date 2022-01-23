package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;

import java.util.function.Consumer;

public class DocumentConfig extends Definition {
  private final Token name;
  private final Token equals;
  private final Expression value;
  private final Token semicolon;

  public DocumentConfig(Token name, Token equals, Expression value, Token semicolon) {
    this.name = name;
    this.equals = equals;
    this.value = value;
    this.semicolon = semicolon;
    ingest(name, equals, semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(name);
    yielder.accept(equals);
    value.emit(yielder);
    yielder.accept(semicolon);
  }

  @Override
  public void typing(Environment environment) {
    environment.rules.IsInteger(value.typing(environment, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)), false);
  }
}
