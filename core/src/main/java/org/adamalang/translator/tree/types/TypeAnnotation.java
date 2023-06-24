/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;

import java.util.ArrayList;
import java.util.function.Consumer;

/** a list of annotations to apply to a type; this is for reflection to do some magic */
public class TypeAnnotation {

  public static class Annotation {
    public final Token name;
    public final Token equals;
    public final Token value;

    public Annotation(Token name, Token equals, Token value) {
      this.name = name;
      this.equals = equals;
      this.value = value;
    }
  }

  public final Token open;
  public final ArrayList<TokenizedItem<Annotation>> annotations;
  public final Token close;

  public TypeAnnotation(Token open, ArrayList<TokenizedItem<Annotation>> annotations, Token close) {
    this.open = open;
    this.annotations = annotations;
    this.close = close;
  }

  public void emit(Consumer<Token> yielder) {
    yielder.accept(open);
    for (TokenizedItem<Annotation> annotation : annotations) {
      annotation.emitBefore(yielder);
      yielder.accept(annotation.item.name);
      if (annotation.item.equals != null) {
        yielder.accept(annotation.item.equals);
        yielder.accept(annotation.item.value);
      }
      annotation.emitAfter(yielder);
    }
    yielder.accept(close);
  }
}
