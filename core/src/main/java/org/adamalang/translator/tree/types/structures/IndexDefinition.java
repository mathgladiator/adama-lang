/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.parser.token.Token;

import java.util.function.Consumer;

public class IndexDefinition extends StructureComponent {
  public final Token indexToken;
  public final Token nameToken;
  public final Token semicolonToken;

  public IndexDefinition(final Token indexToken, final Token nameToken, final Token semicolonToken) {
    this.indexToken = indexToken;
    this.nameToken = nameToken;
    this.semicolonToken = semicolonToken;
    ingest(indexToken);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(indexToken);
    yielder.accept(nameToken);
    yielder.accept(semicolonToken);
  }
}
