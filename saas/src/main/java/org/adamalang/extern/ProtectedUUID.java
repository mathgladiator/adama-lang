/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.UUID;

public class ProtectedUUID {
  private static char[] UUID_CODEC_BASE =  new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','Y','Z'};

  private static String encode(UUID id) {
    try {
      StringBuilder sb = new StringBuilder();
      long v = id.getLeastSignificantBits();
      long trailer = 1;
      if (v < 0) {
        v = -v;
        trailer *= 2 + 1;
      }
      int m = UUID_CODEC_BASE.length;
      while (v > 0) {
        sb.append(UUID_CODEC_BASE[(int) (v % m)]);
        v /= m;
      }
      v = id.getMostSignificantBits();
      if (v < 0) {
        v = -v;
        trailer *= 2 + 1;
      }
      while (v > 0) {
        sb.append(UUID_CODEC_BASE[(int) (v % m)]);
        v /= m;
      }
      while (trailer > 0) {
        sb.append(UUID_CODEC_BASE[(int) (trailer % m)]);
        trailer /= m;
      }
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
      sb.append('X');
      v = Math.abs(digest[0] + digest[1] * 256 + digest[2] * 256 * 256);
      int signbytes = 2;
      while (v > 0 && signbytes > 0) {
        sb.append(UUID_CODEC_BASE[(int) (v % m)]);
        v /= m;
        signbytes--;
      }
      return sb.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String generate() {
    return encode(UUID.randomUUID());
  }
}
