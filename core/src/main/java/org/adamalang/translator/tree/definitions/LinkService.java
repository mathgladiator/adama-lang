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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

/** link a known service into the specification */
public class LinkService extends Definition {
  private final Token link;
  public final Token name;
  private final Token open;
  private final Token close;
  private final ArrayList<Consumer<Consumer<Token>>> emission;
  public final ArrayList<DefineService.ServiceAspect> aspects;

  public LinkService(Token link, Token name, Token open, ArrayList<Consumer<Consumer<Token>>> emission, ArrayList<DefineService.ServiceAspect> aspects, Token close) {
    this.link = link;
    this.name = name;
    this.open = open;
    this.emission = emission;
    this.aspects = aspects;
    this.close = close;
    ingest(link);
    ingest(close);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(link);
    yielder.accept(name);
    yielder.accept(open);
    for (Consumer<Consumer<Token>> emitter : emission) {
       emitter.accept(yielder);
    }
    yielder.accept(close);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(link);
    formatter.endLine(close);
  }

  public String toParams() {
    StringBuilder sb = new StringBuilder();
    for (Consumer<Consumer<Token>> emitter : emission) {
      emitter.accept((t) -> {
        if (t.nonSemanticTokensPrior != null) {
          for (Token prior : t.nonSemanticTokensPrior) {
            sb.append(prior.text);
          }
        }
        sb.append(t.text);
        if (t.nonSemanticTokensAfter != null) {
          for (Token next : t.nonSemanticTokensAfter) {
            sb.append(next.text);
          }
        }
      });
    }
    return sb.toString();
  }

  public HashSet<String> names() {
    HashSet<String> names = new HashSet<>();
    for (DefineService.ServiceAspect aspect : aspects) {
      names.add(aspect.name.text);
    }
    return names;
  }
}
