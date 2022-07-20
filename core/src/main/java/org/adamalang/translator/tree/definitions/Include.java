/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;

import java.util.function.Consumer;

public class Include extends Definition {
  private final Token include;
  public final Token resource;
  private final Token semicolon;

  public Include(Token include, Token resource, Token semicolon) {
    this.include = include;
    this.resource = resource;
    this.semicolon = semicolon;
    ingest(include);
    ingest(semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(include);
    yielder.accept(resource);
    yielder.accept(semicolon);
  }

  @Override
  public void typing(Environment environment) {

  }
}
