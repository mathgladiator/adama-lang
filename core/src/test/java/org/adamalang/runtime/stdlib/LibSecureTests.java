/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibSecureTests {
  @Test
  public void errors() {
    try {
      LibSecure.hashPasswordV1("password", "z");
      Assert.fail();
    } catch (final RuntimeException re) {
      Assert.assertEquals("java.lang.RuntimeException: not hex due to even", re.getMessage());
    }
  }

  @Test
  public void flow() {
    Assert.assertEquals(32, LibSecure.generateSalt16().length());
    final var salt = "ABABABABABABABABABABABABABABABAB";
    final var hash = LibSecure.hashPasswordV1("password", salt);
    Assert.assertEquals("bff077fa671d3c09cb7f027e05a9fee529c3f0434c61f5bebc9056369fbed14f", hash);
    Assert.assertTrue(LibSecure.stringEquals(hash, hash));
    Assert.assertFalse(LibSecure.stringEquals(hash, salt));
    Assert.assertFalse(LibSecure.stringEquals(hash + "x", hash + "z"));
    Assert.assertFalse(LibSecure.stringEquals(hash, null));
    Assert.assertFalse(LibSecure.stringEquals(null, hash));
    try {
      LibSecure.hashPasswordV1("password", "salt");
      Assert.fail();
    } catch (final RuntimeException re) {
      Assert.assertEquals("java.lang.RuntimeException: invalid hex character", re.getMessage());
    }
  }
}
