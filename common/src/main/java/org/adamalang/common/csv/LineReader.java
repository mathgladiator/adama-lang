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
package org.adamalang.common.csv;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

/** assuming a reader parsed out lines, this will parse a complete CSV line */
public class LineReader {
  /** parse a line assuming a well parsed line */
  public static String[] parse(String ln) {
    ArrayList<String> parts = new ArrayList<>();
    StringBuilder current = new StringBuilder();

    Runnable cut = () -> {
      parts.add(current.toString());
      current.setLength(0);
    };

    PrimitiveIterator.OfInt it = ln.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.nextInt();
      switch (cp) {
        case ',':
          cut.run();
          break;
        case '"':
          while (it.hasNext()) {
            int cp2 = it.nextInt();
            if (cp2 == '"') {
              if (it.hasNext()) {
                int cp3 = it.nextInt();
                if (cp3 == '"') {
                  current.append(Character.toString(cp3));
                } else {
                  cut.run();
                  break;
                }
              } else {
                break;
              }
            } else {
              current.append(Character.toString(cp2));
            }
          }
          break;
        default:
          current.append(Character.toString(cp));
      }
    }
    cut.run();
    return parts.toArray(new String[parts.size()]);
  }
}
