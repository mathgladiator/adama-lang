/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.parser.token.Token;

import java.util.function.Consumer;

public class IndexDefinition extends StructureComponent {
  public final Token indexToken;
  public final Token nameToken;
  public final Token semicolonToken;

  public IndexDefinition(
      final Token indexToken, final Token nameToken, final Token semicolonToken) {
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
