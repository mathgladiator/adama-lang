/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;

import java.util.ArrayList;
import java.util.function.Consumer;

/** a list of annotations to apply to a type; this is for reflection to do some magic */
public class TypeAnnotation {
  public final Token open;
  public final ArrayList<TokenizedItem<Token>> annotations;
  public final Token close;

  public TypeAnnotation(Token open, ArrayList<TokenizedItem<Token>> annotations, Token close) {
    this.open = open;
    this.annotations = annotations;
    this.close = close;
  }

  public void emit(Consumer<Token> yielder) {
    yielder.accept(open);
    for (TokenizedItem<Token> annotation : annotations) {
      annotation.emitBefore(yielder);
      yielder.accept(annotation.item);
      annotation.emitAfter(yielder);
    }
    yielder.accept(close);
  }
}
