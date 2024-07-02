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
package org.adamalang.common.html;

import java.util.Iterator;

/** inject coordinates into HTML */
public class InjectCoordInline {
  public static String execute(String html, String name) {
    Iterator<Token> it = Tokenizer.of(html);
    StringBuilder sb = new StringBuilder();
    while (it.hasNext()) {
      Token token = it.next();
      if (token.type == Type.ElementOpen) {
        final String prefix;
        final String suffix;
       if (token.text.endsWith("/>")) {
         prefix = token.text.substring(0, token.text.length() - 2);
         suffix = " />";
        } else {
         prefix = token.text.substring(0, token.text.length() - 1);
         suffix = ">";
       }
       int leftCut = prefix.stripTrailing().length();
       String right = leftCut == prefix.length() ? "" : prefix.substring(leftCut);
       sb.append(prefix, 0, leftCut).append(" ln:ch=\"").append(token.coords()).append(";").append(name).append("\"").append(right).append(suffix);
      } else {
        sb.append(token.text);
      }
    }
    return sb.toString();
  }
}
