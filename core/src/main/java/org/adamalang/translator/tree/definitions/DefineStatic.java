/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.definitions.config.DefineDocumentEvent;
import org.adamalang.translator.tree.definitions.config.DocumentConfig;
import org.adamalang.translator.tree.definitions.config.StaticPiece;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyInternalReadonlyClass;

import java.util.ArrayList;
import java.util.function.Consumer;

/** group all the static methods and properties here */
public class DefineStatic extends Definition {
  public final Token openContext;
  public final Token contextName;
  public final Token closeContext;
  public final ArrayList<DefineDocumentEvent> events;
  public final ArrayList<DocumentConfig> configs;
  private final Token staticToken;
  private final Token openToken;
  private final ArrayList<StaticPiece> definitions;
  private final Token closeToken;

  public DefineStatic(Token staticToken, Token openContext, Token contextName, Token closeContext, Token openToken, ArrayList<StaticPiece> definitions, Token closeToken) {
    this.staticToken = staticToken;
    this.openContext = openContext;
    this.contextName = contextName;
    this.closeContext = closeContext;
    this.openToken = openToken;
    this.definitions = definitions;
    this.closeToken = closeToken;
    this.events = new ArrayList<>();
    this.configs = new ArrayList<>();
    for (StaticPiece definition : definitions) {
      if (definition instanceof DefineDocumentEvent) {
        events.add((DefineDocumentEvent) definition);
        if (contextName != null) {
          ((DefineDocumentEvent) definition).setContextVariable(contextName.text);
        }
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
    if (openContext != null) {
      yielder.accept(openContext);
      yielder.accept(contextName);
      yielder.accept(closeContext);
    }
    yielder.accept(openToken);
    for (StaticPiece definition : definitions) {
      definition.emit(yielder);
    }
    yielder.accept(closeToken);
  }

  @Override
  public void format(Formatter formatter) {
    for (StaticPiece defn : definitions) {
      defn.format(formatter);
    }
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    if (contextName != null) {
      fe.define(contextName.text);
    }
    checker.register(fe.free, (environment) -> {
      Environment next = environment.staticPolicy().scopeStatic();
      if (contextName != null) {
        next.define(contextName.text, new TyInternalReadonlyClass(CoreRequestContext.class), true, this);
      }
      for (StaticPiece definition : definitions) {
        definition.typing(next);
      }
    });
  }
}
