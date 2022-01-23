/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.Document;

import java.util.ArrayList;
import java.util.function.Consumer;

/** group all the static methods and properties here */
public class DefineStatic extends Definition {
  private final Token staticToken;
  private final Token openToken;
  private final ArrayList<Definition> definitions;
  private final Token closeToken;
  public final ArrayList<DefineDocumentEvent> events;
  public final ArrayList<DocumentConfig> configs;

  public DefineStatic(Token staticToken, Token openToken, ArrayList<Definition> definitions, Token closeToken) {
    this.staticToken = staticToken;
    this.openToken = openToken;
    this.definitions = definitions;
    this.closeToken = closeToken;
    this.events = new ArrayList<>();
    this.configs = new ArrayList<>();
    for (Definition definition : definitions) {
      if (definition instanceof DefineDocumentEvent) {
        events.add((DefineDocumentEvent) definition);
      }
      if (definition instanceof DocumentConfig) {
        configs.add((DocumentConfig) definition);
      }
    }
    ingest(staticToken, openToken, closeToken);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(staticToken);
    yielder.accept(openToken);
    for (Definition definition : definitions) {
      definition.emit(yielder);
    }
    yielder.accept(closeToken);
  }

  @Override
  public void typing(Environment environment) {
    Environment next = environment.staticPolicy().scopeStatic();
    for (Definition definition : definitions) {
      definition.typing(next);
    }
  }
}
