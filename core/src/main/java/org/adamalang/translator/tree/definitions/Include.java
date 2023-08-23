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
  private final Token[] resources;
  private final Token semicolon;
  public final String import_name;

  public Include(Token include, Token[] resources, Token semicolon) {
    this.include = include;
    this.resources = resources;
    this.semicolon = semicolon;
    ingest(include);
    ingest(semicolon);
    StringBuilder name = new StringBuilder();
    for (Token token : resources) {
      name.append(token.text);
    }
    this.import_name = name.toString();
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(include);
    for (Token token : resources) {
      yielder.accept(token);
    }
    yielder.accept(semicolon);
  }
}
