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
