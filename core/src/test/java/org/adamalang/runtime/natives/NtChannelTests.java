/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.async.*;
import org.adamalang.runtime.contracts.CanConvertToObject;
import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class NtChannelTests {
    public static final CanConvertToObject DEMO = new CanConvertToObject() {
        @Override
        public ObjectNode convertToObjectNode() {
            return Utility.createObjectNode();
        }
    };
    @Test
    public void flow_nope() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        SimpleFuture<String> future = channel.fetch(NtClient.NO_ONE);
        Assert.assertFalse(future.exists());
        try {
            future.await();
            Assert.fail();
        } catch (ComputeBlockedException bce) {
        }
    }
    @Test
    public void flow() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", Utility.createObjectNode()), "X");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        SimpleFuture<String> future = channel.fetch(NtClient.NO_ONE);
        Assert.assertTrue(future.exists());
    }
    @Test
    public void flow_decide_nope() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        Assert.assertFalse(channel.decide(NtClient.NO_ONE, new CanConvertToObject[0]).await().has());
    }
    @Test
    public void flow_choose_nope() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        Assert.assertFalse(channel.choose(NtClient.NO_ONE, new CanConvertToObject[0], 3).await().has());
    }
    @Test
    public void flow_decide_options_nothing_available() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        SimpleFuture<NtMaybe<String>> future = channel.decide(NtClient.NO_ONE, new CanConvertToObject[] { DEMO, DEMO});
        try {
            future.await();
            Assert.fail();
        } catch (ComputeBlockedException cbe) {
        }
    }
    @Test
    public void flow_choose_options_nothing_available() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        SimpleFuture<NtMaybe<String>> future = channel.choose(NtClient.NO_ONE, new CanConvertToObject[] { DEMO, DEMO}, 2);
        try {
            future.await();
            Assert.fail();
        } catch (ComputeBlockedException cbe) {
        }
    }
    @Test
    public void flow_decide_options_available() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", Utility.createObjectNode()), "X");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        SimpleFuture<NtMaybe<String>> future = channel.decide(NtClient.NO_ONE, new CanConvertToObject[] { DEMO, DEMO});
        future.await();
    }
    @Test
    public void flow_choose_options_available() {
        RxInt32 key = new RxInt32(null, 42);
        OutstandingFutureTracker futures = new OutstandingFutureTracker(key);
        Sink<String> sink = new Sink<>("channel");
        sink.enqueue(new AsyncTask(0, NtClient.NO_ONE, "channel", Utility.createObjectNode()), "X");
        NtChannel<String> channel = new NtChannel<>(futures, sink);
        SimpleFuture<NtMaybe<String>> future = channel.choose(NtClient.NO_ONE, new CanConvertToObject[] { DEMO, DEMO}, 2);
        future.await();
    }
}
