/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
