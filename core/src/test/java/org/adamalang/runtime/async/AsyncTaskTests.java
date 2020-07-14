/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTaskTests {
    @Test
    public void ideal_flow() throws Exception {
        AsyncTask at = new AsyncTask(0, NtClient.NO_ONE, "ch", Utility.createObjectNode());
        AtomicInteger ref = new AtomicInteger(0);
        at.setAction(() -> {
            ref.incrementAndGet();
        });
        at.execute();
    }

    @Test
    public void abort_flow() {
        AsyncTask at = new AsyncTask(0, NtClient.NO_ONE, "ch", Utility.createObjectNode());
        at.setAction(() -> {
            throw new AbortMessageException();
        });
        try {
            at.execute();
            Assert.fail();
        } catch (RetryProgressException rpe) {
        }
    }
}
