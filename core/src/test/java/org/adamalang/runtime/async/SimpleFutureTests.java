/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class SimpleFutureTests {
    @Test
    public void no_value() {
        SimpleFuture<String> sf = new SimpleFuture<>("chan", NtClient.NO_ONE, null);
        Assert.assertFalse(sf.exists());
        try {
            sf.await();
            Assert.fail();
        } catch (ComputeBlockedException cbe) {
        }
    }

    @Test
    public void has_value() {
        SimpleFuture<String> sf = new SimpleFuture<>("chan", NtClient.NO_ONE, "cake");
        Assert.assertTrue(sf.exists());
        Assert.assertEquals("cake", sf.await());
    }
}
