package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WebUri extends Definition {
  private final Token base;
  private final ArrayList<Consumer<Consumer<Token>>> emission;
  private final HashMap<String, TyType> variables;

  public WebUri(Token base) {
    this.base = base;
    this.emission = new ArrayList<>();
    this.variables = new HashMap<>();
    ingest(base);
  }

  public void extend(Token slash, Token dollarSign, Token id, Token colon, TyType type) {
    ingest(slash);
    emission.add((y) -> y.accept(slash));
    if (dollarSign != null) {
      emission.add((y) -> y.accept(dollarSign));
    }
    emission.add((y) -> y.accept(id));
    if (colon != null) {
      ingest(type);
      emission.add((y) -> y.accept(colon));
      emission.add((y) -> type.emit(y));
      variables.put(id.text, type);
    }
  }

  public void extendInto(Environment environment) {
    for (Map.Entry<String, TyType> var : variables.entrySet()) {
      environment.define(var.getKey(), var.getValue(), true, this);
    }
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(base);
    for (Consumer<Consumer<Token>> emit : emission) {
      emit.accept(yielder);
    }
  }

  @Override
  public void typing(Environment environment) {
    for (Map.Entry<String, TyType> var : variables.entrySet()) {
      TyType typeToCheck = environment.rules.Resolve(var.getValue(), false);
      boolean valid = typeToCheck instanceof TyNativeInteger || typeToCheck instanceof TyNativeDouble || typeToCheck instanceof TyNativeLong || typeToCheck instanceof TyNativeString || typeToCheck instanceof TyNativeBoolean;
      if (!valid) {
        environment.document.createError(this, "The parameter type must be int, long, double, string, or boolean", "WebUri");
      }
    }
  }
}
