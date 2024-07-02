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
package org.adamalang.runtime.stdlib;

/**
 * this encodes an ID in a pseudo-cryptic way that is pretty; it is purely cosmetic, but it does
 * encode in a way that brings the most change forward so it is appropriate to use in a prefix based
 * system like S3
 */
public class IdCodec {
  private static final char[] TABLE_INT_TO_CH = new char[]{'A', 'J', '8', 'N', 'F', 'W', 'S', 'X', '7', 'D', 'Q', 'M', 'R', 'P', 'Y', 'E', 'I', 'O', '3', '5', 'C', 'V', '6', 'B', 'H', 'T', '2', 'U', 'K', 'L', '9', '4', 'G'};
  private static final int[] TABLE_CH_TO_INT = buildDecoderTable(TABLE_INT_TO_CH);

  private static int[] buildDecoderTable(char[] table) {
    int[] decoder = new int[40];
    for (int k = 0; k < decoder.length; k++) {
      decoder[k] = 0;
    }
    for (int k = 0; k < table.length; k++) {
      decoder[(table[k] - '2')] = k;
    }
    return decoder;
  }

  public static String encode(long value) {
    StringBuilder sb = new StringBuilder();
    long v = value;
    int len = 0;
    do {
      int low = (int) (v % 33);
      sb.append(TABLE_INT_TO_CH[low]);
      v -= low;
      v /= 33;
      len++;
    } while (v > 0 || len < 7);
    return sb.toString();
  }

  public static long decode(String str) {
    long value = 0;
    for (int k = str.length() - 1; k >= 0; k--) {
      value *= 33;
      value += TABLE_CH_TO_INT[str.charAt(k) - '2'];
    }
    return value;
  }
}
