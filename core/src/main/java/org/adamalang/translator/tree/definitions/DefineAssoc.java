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

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.TreeSet;
import java.util.function.Consumer;

public class DefineAssoc extends Definition {
  private final Token assoc;
  public final Token name;
  public final Token open;
  public final Token fromTypeName;
  public final Token comma;
  public final Token toTypeName;
  public final Token close;
  private final Token semicolon;
  public short id;

  public DefineAssoc(Token assoc, Token open, Token fromTypeName, Token comma, Token toTypeName, Token close, Token name, Token semicolon) {
    this.assoc = assoc;
    this.open = open;
    this.fromTypeName = fromTypeName;
    this.comma = comma;
    this.toTypeName = toTypeName;
    this.close = close;
    this.name = name;
    this.semicolon = semicolon;
    this.id = 0;
    ingest(assoc);
    ingest(semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(assoc);
    yielder.accept(open);
    yielder.accept(fromTypeName);
    yielder.accept(comma);
    yielder.accept(toTypeName);
    yielder.accept(close);
    yielder.accept(name);
    yielder.accept(semicolon);
  }

  public void typing(TypeCheckerRoot checker) {
    TreeSet<String> depends = new TreeSet<>();
    depends.add(fromTypeName.text);
    depends.add(toTypeName.text);
    checker.register(depends, (env) -> {
      TyType fromT = env.document.types.get(fromTypeName.text);
      if (fromT == null) {
        checker.issueError(DefineAssoc.this, "The type '" + fromTypeName.text + "' was not found");
      }
      TyType toT = env.document.types.get(toTypeName.text);
      if (toT == null) {
        checker.issueError(DefineAssoc.this, "The type '" + toTypeName.text + "' was not found");
      }
      env.rules.IsRxStructure(fromT, false);
      env.rules.IsRxStructure(toT, false);
    });
  }
}
