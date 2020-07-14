/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.linq;

import java.util.function.Consumer;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

public class OrderPair extends DocumentPosition {
  public final boolean asc;
  public final Token ascToken;
  public final Token commaToken;
  public final String name;
  public final Token nameToken;

  public OrderPair(final Token commaToken, final Token nameToken, final Token ascToken) {
    this.commaToken = commaToken;
    this.nameToken = nameToken;
    this.ascToken = ascToken;
    name = nameToken.text;
    if (ascToken != null && ascToken.text.equals("desc")) {
      asc = false;
    } else {
      asc = true;
    }
    if (commaToken != null) {
      ingest(commaToken);
    }
    ingest(nameToken);
    if (ascToken != null) {
      ingest(ascToken);
    }
  }

  public void emit(final Consumer<Token> yielder) {
    if (commaToken != null) {
      yielder.accept(commaToken);
    }
    yielder.accept(nameToken);
    if (ascToken != null) {
      yielder.accept(ascToken);
    }
  }
}
