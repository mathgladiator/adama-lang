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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/** an ID that is opaque to users */
public class ProtectedUUID {
  private static final char[] UUID_CODEC_BASE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Y', 'Z'};

  public static String generate() {
    return encode(UUID.randomUUID());
  }

  public static String encode(UUID id) {
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
}
