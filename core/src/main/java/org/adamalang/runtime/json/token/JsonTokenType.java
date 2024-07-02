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
package org.adamalang.runtime.json.token;

/** defines the type of a token found within a file */
public enum JsonTokenType {
  EndArray(false), // ]
  EndObject(false), // }
  False(false), // false
  Null(false), // null
  NumberLiteralInteger(false), // see data: parse it
  NumberLiteralDouble(false), // see data: parse it
  StartArray(false), // [
  StartObject(false), // {
  StringLiteral(false), // see data: use it
  True(false); // true

  /** is the token hidden from the parse tree */
  public final boolean hidden;

  JsonTokenType(final boolean hidden) {
    this.hidden = hidden;
  }
}
