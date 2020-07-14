/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AdamaSessionTests {
    @Test
    public void flow() {
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        AtomicInteger ai = new AtomicInteger(0);
        session.subscribeToSessionDeath(() -> ai.getAndIncrement());
        Assert.assertEquals(0, ai.get());
        Assert.assertTrue(session.isAlive());
        session.kill();
        Assert.assertFalse(session.isAlive());
        Assert.assertEquals(1, ai.get());
        session.subscribeToSessionDeath(() -> ai.getAndIncrement());
        Assert.assertEquals(2, ai.get());
    }
}
