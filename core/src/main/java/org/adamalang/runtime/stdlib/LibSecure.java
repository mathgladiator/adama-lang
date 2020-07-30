/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** some security based functions */
public class LibSecure {
  protected static char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  protected static byte[] decodeHex(final char[] data) {
    if (data.length % 2 == 1) { throw new RuntimeException("not hex due to even"); }
    final var bytes = new byte[data.length / 2];
    for (var i = 0; i + 1 < data.length; i += 2) {
      final var hi = Character.digit(data[i], 16) * 16;
      final var lo = Character.digit(data[i + 1], 16);
      if (hi < 0 || lo < 0) { throw new RuntimeException("invalid hex character"); }
      bytes[i / 2] = (byte) (hi + lo & 0xFF);
    }
    return bytes;
  }

  protected static char[] encodeHex(final byte[] data) {
    final var output = new char[data.length * 2];
    for (var i = 0; i < data.length; i++) {
      output[i * 2] = HEX_DIGITS[(0xF0 & data[i]) / 16];
      output[i * 2 + 1] = HEX_DIGITS[0x0F & data[i]];
    }
    return output;
  }

  public static String generateSalt16() {
    final var salt = SecureRandom.getSeed(16);
    return new String(encodeHex(salt));
  }

  public static String hashPasswordV1(final String password, final String salt) {
    try {
      final var skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      final var spec = new PBEKeySpec(password.toLowerCase().toCharArray(), decodeHex(salt.toCharArray()), 1024, 256);
      final var key = skf.generateSecret(spec);
      final var res = key.getEncoded();
      return new String(encodeHex(res));
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
