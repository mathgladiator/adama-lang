/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibSecureTests {
    @Test
    public void flow() {
        Assert.assertEquals(32, LibSecure.generateSalt16().length());
        String salt = "ABABABABABABABABABABABABABABABAB";
        String hash = LibSecure.hashPasswordV1("password", salt);
        Assert.assertEquals("bff077fa671d3c09cb7f027e05a9fee529c3f0434c61f5bebc9056369fbed14f", hash);
        Assert.assertTrue(LibSecure.stringEquals(hash, hash));
        Assert.assertFalse(LibSecure.stringEquals(hash, salt));
        Assert.assertFalse(LibSecure.stringEquals(hash + "x", hash + "z"));
        Assert.assertFalse(LibSecure.stringEquals(hash, null));
        Assert.assertFalse(LibSecure.stringEquals(null, hash));
        try {
            LibSecure.hashPasswordV1("password", "salt");
            Assert.fail();
        } catch (RuntimeException re) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Illegal hexadecimal character s at index 0", re.getMessage());
        }
    }
}
