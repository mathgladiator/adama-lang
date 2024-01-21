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
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.function.Consumer;

/** a policy that controls how long an item is cached for (once/every-X-time-unit/etc...) */
public class CachePolicy extends DocumentPosition {
  public Token open;
  public Token type;

  // if type == every
  public Token count;
  public Token unit;

  public Token close;

  public CachePolicy(Token open, Token type, Token count, Token unit, Token close) {
    this.open = open;
    this.type = type;
    this.count = count;
    this.unit = unit;
    this.close = close;
    ingest(open);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(open);
    yielder.accept(type);
    if (count != null) {
      yielder.accept(count);
      yielder.accept(unit);
    }
    yielder.accept(close);
  }

  public void format(Formatter formatter) {

  }

  public long toSeconds() {
    if (count == null) {
      return -1;
    } else {
      int v = Integer.parseInt(count.text);
      switch (unit.text) {
        case "hr":
        case "hour":
        case "hours":
          return v * 60 * 60;
        case "min":
        case "minute":
        case "minutes":
          return v * 60;
        default:
          return v; // seconds
      }
    }
  }
}
