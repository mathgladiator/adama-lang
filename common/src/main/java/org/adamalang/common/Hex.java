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

/** Hex encodings */
public class Hex {
  private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
  private static final char[] HEX_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  public static String of(final byte[] bytes) {
    final int n = bytes.length;
    final char[] encoded = new char[n * 2];
    int j = 0;
    for (int i = 0; i < n; i++) {
      encoded[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
      encoded[j++] = HEX[0x0F & bytes[i]];
    }
    return new String(encoded);
  }

  public static String of(final byte b) {
    final char[] encoded = new char[2];
    encoded[0] = HEX[(0xF0 & b) >>> 4];
    encoded[1] = HEX[0x0F & b];
    return new String(encoded);
  }

  public static String of_upper(final byte[] bytes) {
    final int n = bytes.length;
    final char[] encoded = new char[n * 2];
    int j = 0;
    for (int i = 0; i < n; i++) {
      encoded[j++] = HEX_UPPER[(0xF0 & bytes[i]) >>> 4];
      encoded[j++] = HEX_UPPER[0x0F & bytes[i]];
    }
    return new String(encoded);
  }

  public static String of_upper(final byte b) {
    final char[] encoded = new char[2];
    encoded[0] = HEX_UPPER[(0xF0 & b) >>> 4];
    encoded[1] = HEX_UPPER[0x0F & b];
    return new String(encoded);
  }

  public static byte[] from(String hex) {
    byte[] result = new byte[hex.length() >> 1];
    int at = 0;
    for (int k = 0; k < hex.length(); k += 2) {
      result[at] = (byte) ((single(hex.charAt(k)) << 4) + single(hex.charAt(k + 1)));
      at++;
    }
    return result;
  }

  public static int single(char hex) {
    if ('0' <= hex && hex <= '9') {
      return hex - '0';
    }
    if ('a' <= hex && hex <= 'f') {
      return 10 + (hex - 'a');
    }
    if ('A' <= hex && hex <= 'F') {
      return 10 + (hex - 'A');
    }
    return 0;
  }
}
