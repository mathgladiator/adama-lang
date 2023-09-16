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

import java.util.regex.Pattern;

/** help with strings */
public class StringHelper {

  /** split the given text by \n, and then rejoin with each line prefixed by the given tab */
  public static String splitNewlineAndTabify(String text, String tab) {
    StringBuilder sb = new StringBuilder();
    String[] lines = text.split(Pattern.quote("\n"));
    for (String ln : lines) {
      sb.append(tab + ln.stripTrailing() + "\n");
    }
    return sb.toString();
  }
}
