/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class SinkTests {
    @Test
    public void flow_in_and_out() {
        Sink<String> sink = new Sink<>("channel");
        sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", Utility.createObjectNode()), "Cake");
        SimpleFuture<String> sf = sink.dequeue(NtClient.NO_ONE);
        Assert.assertTrue(sf.exists());
        Assert.assertEquals("Cake", sf.await());
        SimpleFuture<String> sf2 = sink.dequeue(NtClient.NO_ONE);
        Assert.assertFalse(sf2.exists());
    }

    @Test
    public void maybe_out_no_data() {
        Sink<String> sink = new Sink<>("channel");
        SimpleFuture<NtMaybe<String>> sf = sink.dequeueMaybe(NtClient.NO_ONE);
        Assert.assertFalse(sf.exists());
    }

    @Test
    public void maybe_out_with_data() {
        Sink<String> sink = new Sink<>("channel");
        sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", Utility.createObjectNode()), "Cake");
        SimpleFuture<NtMaybe<String>> sf = sink.dequeueMaybe(NtClient.NO_ONE);
        Assert.assertTrue(sf.exists());
        Assert.assertEquals("Cake", sf.await().get());
        SimpleFuture<String> sf2 = sink.dequeue(NtClient.NO_ONE);
        Assert.assertFalse(sf2.exists());
    }

    @Test
    public void flow_in_clear_out() {
        Sink<String> sink = new Sink<>("channel");
        sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", Utility.createObjectNode()), "Cake");
        sink.clear();
        SimpleFuture<String> sf2 = sink.dequeue(NtClient.NO_ONE);
        Assert.assertFalse(sf2.exists());
    }
}
