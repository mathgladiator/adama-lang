/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.structures;

import java.util.function.Consumer;
import org.adamalang.translator.parser.token.Token;

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
