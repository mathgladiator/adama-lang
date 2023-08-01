/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

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
    return new String(Base64.getEncoder().encode(digest.digest()));
  }
}
