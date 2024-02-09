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

/** common rules for tools */
public class Rules {
  public static boolean isRoot(String tag) {
    if (tag.equalsIgnoreCase("page") || tag.equalsIgnoreCase("template")) {
      return true;
    }
    return false;
  }
  public static boolean isSpecialElement(String tag) {
    String tagNameNormal = Base.normalizeTag(tag);
    try {
      Elements.class.getMethod(tagNameNormal, Environment.class);
      return true;
    } catch (NoSuchMethodException nsme) {
      return false;
    }
  }

  public static boolean isSpecialAttribute(String name) {
    if (name.startsWith("rx:")) {
      return true;
    }
    if (name.equals("href")) {
      return true;
    }
    if (name.equals("for-env")) {
      return true;
    }
    return false;
  }
}
