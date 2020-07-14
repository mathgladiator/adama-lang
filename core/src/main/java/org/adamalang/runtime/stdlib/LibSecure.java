/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.codec.binary.Hex;

/** some security based functions */
public class LibSecure {
  public static String generateSalt16() {
    final var salt = SecureRandom.getSeed(16);
    return Hex.encodeHexString(salt);
  }

  public static String hashPasswordV1(final String password, final String salt) {
    try {
      final var skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      final var spec = new PBEKeySpec(password.toLowerCase().toCharArray(), Hex.decodeHex(salt), 1024, 256);
      final var key = skf.generateSecret(spec);
      final var res = key.getEncoded();
      return Hex.encodeHexString(res);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean stringEquals(final String a, final String b) {
    if (a == b) {
      return true;
    } else if (a != null && b != null) {
      if (a.length() != b.length()) {
        return false;
      } else {
        var result = 0;
        for (var i = 0; i < a.length(); ++i) {
          result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
      }
    } else {
      return false;
    }
  }
}
