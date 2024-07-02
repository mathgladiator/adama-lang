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
package org.adamalang.common;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

/** when we want to encode multiple strings into one string easily */
public class SlashStringArrayEncoder {
  /** encode by packing with slashes as an escaped ('-') delimiter */
  public static String encode(String... fragments) {
    StringBuilder fixed = new StringBuilder();
    boolean appendSlash = false;
    for (String fragment : fragments) {
      if (appendSlash) {
        fixed.append("/");
      } else {
        appendSlash = true;
      }
      PrimitiveIterator.OfInt it = fragment.codePoints().iterator();
      while (it.hasNext()) {
        int cp = it.next();
        switch (cp) {
          case '/':
            fixed.append("-/");
            break;
          case '-':
            fixed.append("--");
            break;
          default:
            fixed.append(Character.toChars(cp));
            break;
        }
      }
    }
    return fixed.toString();
  }

  /** unpack a joined string into an array */
  public static String[] decode(String joined) {
    ArrayList<String> fragments = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    PrimitiveIterator.OfInt it = joined.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.next();
      switch (cp) {
        case '/':
          fragments.add(current.toString());
          current.setLength(0);
          break;
        case '-':
          current.append(Character.toChars(it.next()));
          break;
        default:
          current.append(Character.toChars(cp));
          break;
      }
    }
    fragments.add(current.toString());
    return fragments.toArray(new String[fragments.size()]);
  }
}
