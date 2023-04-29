/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.async.OutstandingFutureTracker;
import org.adamalang.runtime.async.Sink;
import org.adamalang.runtime.async.TimeoutTracker;
import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.algo.HashBuilder;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.reactives.RxInt64;
import org.junit.Assert;
import org.junit.Test;

public class NtChannelTests {
  public static final NtMessageBase DEMO = new NtMessageBase() {
    @Override
    public void __writeOut(JsonStreamWriter writer) {
      writer.beginObject();
      writer.endObject();
    }

    @Override
    public void __ingest(JsonStreamReader reader) {
      reader.skipValue();
    }

    @Override
    public void __hash(HashBuilder __hash) {
      __hash.hashString("demo");
    }
  };

  public OutstandingFutureTracker makeFutures() {
    final var src = new RxInt32(null, 42);
    final var futures = new OutstandingFutureTracker(src, new TimeoutTracker(new RxInt64(null, 0), new RxInt64(null, 0)));
    return futures;
  }

  @Test
  public void flow1() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "X");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.fetchItem(NtPrincipal.NO_ONE);
    Assert.assertTrue(future.exists());
  }

  @Test
  public void flow2() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "X");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.fetchArray(NtPrincipal.NO_ONE);
    Assert.assertTrue(future.exists());
  }

  @Test
  public void flow_choose_nope() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    final var channel = new NtChannel<>(futures, sink);
    Assert.assertFalse(channel.choose(NtPrincipal.NO_ONE, new NtMessageBase[0], 3).await().has());
  }

  @Test
  public void flow_choose_options_available() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "X");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.choose(NtPrincipal.NO_ONE, new NtMessageBase[] {DEMO, DEMO}, 2);
    future.await();
  }

  @Test
  public void flow_choose_options_nothing_available() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.choose(NtPrincipal.NO_ONE, new NtMessageBase[] {DEMO, DEMO}, 2);
    try {
      future.await();
      Assert.fail();
    } catch (final ComputeBlockedException cbe) {
    }
  }

  @Test
  public void flow_decide_nope() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    final var channel = new NtChannel<>(futures, sink);
    Assert.assertFalse(channel.decide(NtPrincipal.NO_ONE, new NtMessageBase[0]).await().has());
  }

  @Test
  public void flow_decide_options_available() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    sink.enqueue(new AsyncTask(0, NtPrincipal.NO_ONE, "channel", 0, "origin", "ip","message"), "X");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.decide(NtPrincipal.NO_ONE, new NtMessageBase[] {DEMO, DEMO});
    future.await();
  }

  @Test
  public void flow_decide_options_nothing_available() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.decide(NtPrincipal.NO_ONE, new NtMessageBase[] {DEMO, DEMO});
    try {
      future.await();
      Assert.fail();
    } catch (final ComputeBlockedException cbe) {
    }
  }

  @Test
  public void flow_nope() {
    final var futures = makeFutures();
    final var sink = new Sink<String>("channel");
    final var channel = new NtChannel<>(futures, sink);
    final var future = channel.fetchItem(NtPrincipal.NO_ONE);
    Assert.assertFalse(future.exists());
    try {
      future.await();
      Assert.fail();
    } catch (final ComputeBlockedException bce) {
    }
  }
}
