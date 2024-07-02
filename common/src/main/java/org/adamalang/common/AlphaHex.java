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

/** hexidecimal encoding using A-P rather than 0-9A-F; This allows us to stable generate identifiers based on byte[] */
public class AlphaHex {
  private static final char[] HEX = "ABCDEFGHIJKLMNOP".toCharArray();

  public static String encode(byte[] bytes) {
    char[] chs = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      chs[j * 2] = HEX[v >>> 4];
      chs[j * 2 + 1] = HEX[v & 0x0F];
    }
    return new String(chs);
  }
}
