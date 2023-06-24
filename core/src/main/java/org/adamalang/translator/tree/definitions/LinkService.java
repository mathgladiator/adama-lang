/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;

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
