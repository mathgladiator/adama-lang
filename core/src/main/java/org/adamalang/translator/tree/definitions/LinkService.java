/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;

import java.util.function.Consumer;

/** link a known service into the specification */
public class LinkService extends Definition {
  private final Token link;
  public final Token name;
  private final Token semicolon;

  public LinkService(Token link, Token name, Token semicolon) {
    this.link = link;
    this.name = name;
    this.semicolon = semicolon;
    ingest(link);
    ingest(semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(link);
    yielder.accept(name);
    yielder.accept(semicolon);
  }
}
