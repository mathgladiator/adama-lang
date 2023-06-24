/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;

import java.util.function.Consumer;

/** include a file into the specification */
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
}
