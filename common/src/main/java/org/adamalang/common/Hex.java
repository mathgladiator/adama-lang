/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
  public static byte[] from(String hex) {
    byte[] result = new byte[hex.length() >> 1];
    int at = 0;
    for (int k = 0; k < hex.length(); k += 2) {
      result[at] = (byte)((single(hex.charAt(k)) << 4) + single(hex.charAt(k + 1)));
      at++;
    }
    return result;
  }
}
