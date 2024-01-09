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
package org.adamalang.rxhtml.template;

import org.adamalang.common.Escaping;

public class Escapes {
  public static String typeOf(String value) {
    if (value.equals("true") || value.equals("false")) {
      return "bool";
    }
    try {
      Integer.parseInt(value);
      return "int";
    } catch (NumberFormatException nfe) {
    }
    try {
      Double.parseDouble(value);
      return "number";
    } catch (NumberFormatException nfe) {
    }
    return "string";
  }

  public static String constantOf(String value) {
    String type = typeOf(value);
    if ("string".equalsIgnoreCase(type)) {
      return "'" + new Escaping(value).switchQuotes().removeNewLines().go() + "'";
    }
    return value;
  }
}
