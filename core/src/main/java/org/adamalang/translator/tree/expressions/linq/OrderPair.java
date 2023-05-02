/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.function.Consumer;

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
    asc = ascToken == null || !ascToken.text.equals("desc");
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
