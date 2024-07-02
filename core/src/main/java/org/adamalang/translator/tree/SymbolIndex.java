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
package org.adamalang.translator.tree;

import org.adamalang.translator.parser.token.Token;

import java.util.ArrayList;
import java.util.HashMap;

/** compiling will yield a symbol index for advanced IDE support */
public class SymbolIndex {
  public final ArrayList<Token> definitions;
  public final ArrayList<Token> usages;
  public final HashMap<String, Token> perfHover;

  public SymbolIndex() {
    this.definitions = new ArrayList<>();
    this.usages = new ArrayList<>();
    this.perfHover = new HashMap<>();
  }
}
