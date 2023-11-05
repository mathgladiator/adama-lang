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
package org.adamalang.common.html;

/** a very simple HTML token */
public class Token {
  public final Type type;
  public final String text;
  public final int lineStart;
  public final int charStart;
  public final int lineEnd;
  public final int charEnd;

  public Token(Type type, String text, int lineStart, int charStart, int lineEnd, int charEnd) {
    this.type = type;
    this.text = text;
    this.lineStart = lineStart;
    this.charStart = charStart;
    this.lineEnd = lineEnd;
    this.charEnd = charEnd;
  }

  public String coords() {
    return lineStart + ";" + charStart + ";" + lineEnd + ";" + charEnd;
  }
}
