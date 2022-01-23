package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DefineStatic extends Definition {

  private final Token staticToken;
  private final Token openToken;
  private final ArrayList<Definition> definitions;
  private final Token closeToken;

  public final ArrayList<DefineDocumentEvent> events;

  public DefineStatic(Token staticToken, Token openToken, ArrayList<Definition> definitions, Token closeToken) {
    this.staticToken = staticToken;
    this.openToken = openToken;
    this.definitions = definitions;
    this.closeToken = closeToken;
    this.events = new ArrayList<>();
    for (Definition definition : definitions) {
      if (definition instanceof DefineDocumentEvent) {
        events.add((DefineDocumentEvent) definition);
      }
    }
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(staticToken);
    yielder.accept(openToken);
    for (Definition definition : definitions) {
      definition.emit(yielder);
    }
    yielder.accept(closeToken);
  }

  @Override
  public void typing(Environment environment) {
    for (Definition definition : definitions) {
      definition.typing(environment);
    }
  }
}
