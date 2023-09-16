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
package org.adamalang.common;

import java.util.PrimitiveIterator;

/** escaping strings per various rules via flags */
public class Escaping {
  private final String str;
  private boolean escapeDoubleQuote = true;
  private boolean escapeSingleQuote = false;
  private boolean removeReturns = true;
  private boolean escapeReturns = true;
  private boolean keepSlashes = false;
  private boolean removeNewLines = false;

  public Escaping(String str) {
    this.str = str;
  }

  public Escaping switchQuotes() {
    escapeDoubleQuote = !escapeDoubleQuote;
    escapeSingleQuote = !escapeSingleQuote;
    return this;
  }

  public Escaping keepReturns() {
    removeReturns = false;
    return this;
  }

  public Escaping dontEscapeReturns() {
    escapeReturns = false;
    return this;
  }

  public Escaping keepSlashes() {
    this.keepSlashes = true;
    return this;
  }

  public Escaping removeNewLines() {
    this.removeNewLines = true;
    return this;
  }

  @Override
  public String toString() {
    return go();
  }

  public String go() {
    StringBuilder result = new StringBuilder();
    PrimitiveIterator.OfInt it = str.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.nextInt();
      switch (cp) {
        case '\n':
          if (!removeNewLines) {
            result.append("\\n");
          }
          break;
        case '\\':
          if (keepSlashes) {
            result.append("\\");
          } else {
            result.append("\\\\");
          }
          break;
        case '"':
          if (escapeDoubleQuote) {
            result.append("\\\"");
          } else {
            result.append("\"");
          }
          break;
        case '\'':
          if (escapeSingleQuote) {
            result.append("\\'");
          } else {
            result.append("'");
          }
          break;
        case '\r':
          if (!removeReturns) {
            if (escapeReturns) {
              result.append("\\r");
            } else {
              result.append("\r");
            }
          }
          break;
        default:
          result.append(Character.toChars(cp));
      }
    }
    return result.toString();
  }
}
