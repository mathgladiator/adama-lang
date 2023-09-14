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
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;

import java.util.ArrayList;
import java.util.function.Consumer;

/** a way to indicate that a bubble is protected by data */
public class BubbleGuard extends DocumentPosition {
  public final Token open;
  public final ArrayList<TokenizedItem<String>> policies;
  public final Token close;

  public BubbleGuard(Token open, ArrayList<TokenizedItem<String>> policies, Token close) {
    this.open = open;
    this.policies = policies;
    this.close = close;
    ingest(open);
    ingest(close);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(open);
    for (TokenizedItem<String> policy : policies) {
      policy.emitBefore(yielder);
      policy.emitAfter(yielder);
    }
    yielder.accept(close);
  }
}
