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

/** Validators used to protect users, enforce system limits, and keep things tidy */
public class Validators {

  /** Validate that the string is a identifier of sorts with few special characters */
  public static boolean simple(String str, int max) {
    if (str.length() > max) {
      return false;
    }
    for (int k = 0; k < str.length(); k++) {
      char ch = str.charAt(k);
      boolean good = 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z' || '0' <= ch && ch <= '9' || ch == '.' || ch == '-' || ch == '_';
      if (!good) {
        return false;
      }
    }
    return true;
  }
}
