/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.stdlib;

/** this encodes an ID in a pseudo-cryptic way that is pretty; it is purely cosmetic,
 * but it does encode in a way that brings the most change forward so it is appropriate
 * to use in a prefix based system like S3 */
public class IdCodec {
  private static char[] TABLE_INT_TO_CH = new char[] {'A', 'J', '8', 'N', 'F', 'W', 'S', 'X', '7', 'D', 'Q', 'M', 'R', 'P', 'Y', 'E', 'I', 'O', '3', '5', 'C', 'V', '6', 'B', 'H', 'T', '2', 'U', 'K', 'L', '9', '4', 'G'};;

  private static int[] buildDecoderTable(char[] table) {
    int[] decoder = new int[40];
    for(int k = 0; k < decoder.length; k++) {
      decoder[k] = 0;
    }
    for (int k = 0; k <  table.length; k++) {
      decoder[(table[k] - '2')] = k;
    }
    return decoder;
  }

  private static int[] TABLE_CH_TO_INT = buildDecoderTable(TABLE_INT_TO_CH);

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
