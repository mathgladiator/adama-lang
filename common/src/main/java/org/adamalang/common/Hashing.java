/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import java.security.MessageDigest;
import java.util.Base64;

/** helpers for making hashing easy */
public class Hashing {
  public static MessageDigest md5() {
    return forKnownAlgorithm("MD5");
  }

  public static MessageDigest sha384() {
    return forKnownAlgorithm("SHA-384");
  }

  public static MessageDigest sha256() {
    return forKnownAlgorithm("SHA-256");
  }

  public static MessageDigest forKnownAlgorithm(String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String finishAndEncode(MessageDigest digest) {
    return new String(Base64.getEncoder().encode(digest.digest()));
  }
}
