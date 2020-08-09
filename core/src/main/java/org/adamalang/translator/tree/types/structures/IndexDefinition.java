/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
