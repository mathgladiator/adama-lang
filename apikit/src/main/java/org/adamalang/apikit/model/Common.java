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
package org.adamalang.apikit.model;

import java.util.Locale;
import java.util.regex.Pattern;

public class Common {
  public static String camelize(String name) {
    return camelize(name, false);
  }

  public static String camelize(String name, boolean lowerFirst) {
    if (name == null || name.length() == 0) {
      return name;
    }
    String[] parts = name.split("[-/]");
    StringBuilder result = new StringBuilder();
    for (String part : parts) {
      if (lowerFirst) {
        result.append(part.toLowerCase(Locale.ROOT));
      } else {
        result.append(part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1).toLowerCase(Locale.ROOT));
      }
      lowerFirst = false;
    }
    return result.toString();
  }
}
