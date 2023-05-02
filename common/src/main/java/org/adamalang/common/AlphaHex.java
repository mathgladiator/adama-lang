/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
