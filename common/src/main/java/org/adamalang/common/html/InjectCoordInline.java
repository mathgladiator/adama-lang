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

import java.util.Iterator;

/** inject coordinates into HTML */
public class InjectCoordInline {
  public static String execute(String html, String name) {
    Iterator<Token> it = Tokenizer.of(html);
    StringBuilder sb = new StringBuilder();
    while (it.hasNext()) {
      Token token = it.next();
      if (token.type == Type.ElementOpen) {
       if (token.text.endsWith("/>")) {
         sb.append(token.text.substring(0, token.text.length() - 2).stripTrailing() + " ln:ch=\"" + token.coords() + ";" +  name + "\" />");
        } else {
         sb.append(token.text.substring(0, token.text.length() - 1).stripTrailing() + " ln:ch=\"" + token.coords() + ";" + name + "\">");
       }
      } else {
        sb.append(token.text);
      }
    }
    return sb.toString();
  }
}
