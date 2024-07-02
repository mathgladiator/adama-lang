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
import java.util.Base64;

/** helpers for making hashing easy */
public class Hashing {
  public static MessageDigest md5() {
    return forKnownAlgorithm("MD5");
  }

  public static MessageDigest forKnownAlgorithm(String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static MessageDigest sha384() {
    return forKnownAlgorithm("SHA-384");
  }

  public static MessageDigest sha256() {
    return forKnownAlgorithm("SHA-256");
  }

  public static String finishAndEncode(MessageDigest digest) {
    return new String(Base64.getEncoder().encode(digest.digest()), StandardCharsets.UTF_8);
  }

  public static String finishAndEncodeHex(MessageDigest digest) {
    return Hex.of(digest.digest()).toLowerCase();
  }
}
