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
package org.adamalang.runtime.natives.algo;

import java.util.ArrayList;
import java.util.regex.Pattern;

/** parse a string into a array that has ascending and descending encoded for a dynamic compare */
public class DynCompareParser {
  public static CompareField[] parse(String dyn) {
    String[] parts = dyn.split(Pattern.quote(","));
    ArrayList<CompareField> result = new ArrayList<>();
    for (String part : parts) {
      part = part.trim();
      if (part.length() == 0) continue;
      char start = part.charAt(0);
      if (start == '+' || start == '-') {
        result.add(new CompareField(part.substring(1).trim(), start == '-'));
      } else {
        result.add(new CompareField(part, false));
      }
    }
    return result.toArray(new CompareField[result.size()]);
  }
}
