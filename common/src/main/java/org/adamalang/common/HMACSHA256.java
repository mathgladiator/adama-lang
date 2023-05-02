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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class HMACSHA256 {
  /** HmacSHA256(key, data); https://en.wikipedia.org/wiki/HMAC */
  public static byte[] of(final byte[] key, final String data) {
    try {
      final Key keySpec = new SecretKeySpec(key, "HmacSHA256");
      final Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(keySpec);
      return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
