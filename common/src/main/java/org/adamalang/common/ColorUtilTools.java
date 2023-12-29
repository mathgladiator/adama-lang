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

public class ColorUtilTools {
  private static Boolean NO_COLOR;

  private static boolean nocolor() {
    if (NO_COLOR == null) {
      String nc = System.getenv("NO_COLOR");
      NO_COLOR = nc != null && !"0".equals(nc);
    }
    return NO_COLOR;
  }

  public static void setNoColor() {
    NO_COLOR = true;
  }

  public static void lowerNoColor() {
    NO_COLOR = false;
  }

  public static String prefix(String x, ANSI c) {
    if (nocolor()) {
      return x;
    }
    return c.ansi + x + ANSI.Reset.ansi;
  }

  public static String prefixBold(String x, ANSI c) {
    if (nocolor()) {
      return x;
    }
    return ANSI.Bold.ansi + c.ansi + x + ANSI.Reset.ansi;
  }

  public static String justifyLeft(String string, int spacing) {
    return String.format("%-" + spacing + "s", string);
  }
  public static String justifyRight(String string, int spacing) {
    return String.format("%" + spacing + "s", string);
  }
}
