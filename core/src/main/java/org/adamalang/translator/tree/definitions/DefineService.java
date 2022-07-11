/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;

import java.util.ArrayList;
import java.util.function.Consumer;

/** Define a service */
public class DefineService extends Definition {
  public static class ServiceAspect {
    public final Token name;
    public final Token equals;
    public final Expression expression;
    public final Token semicolon;

    public ServiceAspect(Token name, Token equals, Expression expression, Token semicolon) {
      this.name = name;
      this.equals = equals;
      this.expression = expression;
      this.semicolon = semicolon;
    }
  }

  public final Token serviceToken;
  public final Token open;
  public final Token name;
  public final ArrayList<ServiceAspect> aspects;
  public final Token close;

  public DefineService(Token serviceToken, Token open, Token name, ArrayList<ServiceAspect> aspects, Token close) {
    this.serviceToken = serviceToken;
    this.open = open;
    this.name = name;
    this.aspects = aspects;
    this.close = close;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(serviceToken);
    yielder.accept(open);
    yielder.accept(name);
    for (ServiceAspect aspect : aspects) {
      yielder.accept(aspect.name);
      yielder.accept(aspect.equals);
      aspect.expression.emit(yielder);
      yielder.accept(aspect.semicolon);
    }
    yielder.accept(close);
  }

  @Override
  public void typing(Environment environment) {

  }
}
