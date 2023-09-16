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

import java.util.function.Consumer;

/** include a file into the specification */
public class Include extends Definition {
  private final Token include;
  private final Token[] resources;
  private final Token semicolon;
  public final String import_name;

  public Include(Token include, Token[] resources, Token semicolon) {
    this.include = include;
    this.resources = resources;
    this.semicolon = semicolon;
    ingest(include);
    ingest(semicolon);
    StringBuilder name = new StringBuilder();
    for (Token token : resources) {
      name.append(token.text);
    }
    this.import_name = name.toString();
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(include);
    for (Token token : resources) {
      yielder.accept(token);
    }
    yielder.accept(semicolon);
  }
}
