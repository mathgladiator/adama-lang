/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
