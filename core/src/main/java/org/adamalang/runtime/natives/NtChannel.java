/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.async.OutstandingFutureTracker;
import org.adamalang.runtime.async.SimpleFuture;
import org.adamalang.runtime.async.Sink;
import org.adamalang.runtime.async.Timeout;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a channel */
public class NtChannel<T> {
  public final Sink<T> sink;
  public final OutstandingFutureTracker tracker;

  public NtChannel(final OutstandingFutureTracker tracker, final Sink<T> sink) {
    this.tracker = tracker;
    this.sink = sink;
  }

  /** from a list of options, choose $limit of them */
  public SimpleFuture<NtMaybe<T>> choose(final NtPrincipal who, final NtMessageBase[] optionsRaw, final int limit) {
    final var actualLimit = Math.min(limit, optionsRaw.length);
    if (actualLimit == 0) {
      return new SimpleFuture<>(sink.channel, who, new NtMaybe<>());
    }
    final var oldFuture = tracker.make(sink.channel, who);
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("id");
    writer.writeInteger(oldFuture.id);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(oldFuture.channel);
    writer.writeObjectFieldIntro("array");
    writer.writeBoolean(true);
    writer.writeObjectFieldIntro("min");
    writer.writeInteger(limit);
    writer.writeObjectFieldIntro("max");
    writer.writeInteger(limit);
    writer.writeObjectFieldIntro("distinct");
    writer.writeBoolean(true);
    writer.writeObjectFieldIntro("options");
    writer.beginArray();
    for (final NtMessageBase option : optionsRaw) {
      option.__writeOut(writer);
    }
    writer.endArray();
    writer.endObject();
    oldFuture.json = writer.toString();
    final var future = sink.dequeueMaybe(who);
    if (future.exists()) {
      oldFuture.take();
    }
    return future;
  }

  /** from a list of options, pick one of them */
  public SimpleFuture<NtMaybe<T>> decide(final NtPrincipal who, final NtMessageBase[] optionsRaw) {
    if (optionsRaw.length == 0) {
      return new SimpleFuture<>(sink.channel, who, new NtMaybe<>());
    }
    final var oldFuture = tracker.make(sink.channel, who);
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("id");
    writer.writeInteger(oldFuture.id);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(oldFuture.channel);
    writer.writeObjectFieldIntro("array");
    writer.writeBoolean(false);
    writer.writeObjectFieldIntro("min");
    writer.writeInteger(1);
    writer.writeObjectFieldIntro("max");
    writer.writeInteger(1);
    writer.writeObjectFieldIntro("distinct");
    writer.writeBoolean(true);
    writer.writeObjectFieldIntro("options");
    writer.beginArray();
    for (final NtMessageBase option : optionsRaw) {
      option.__writeOut(writer);
    }
    writer.endArray();
    writer.endObject();
    oldFuture.json = writer.toString();
    final var future = sink.dequeueMaybe(who);
    if (future.exists()) {
      oldFuture.take();
    }
    return future;
  }

  /** ask the user for one item, blocks entire universe */
  public SimpleFuture<T> fetchItem(final NtPrincipal who) {
    return fetch(who, false);
  }

  /** ask the user for item/items */
  public SimpleFuture<T> fetch(final NtPrincipal who, boolean array) {
    final var oldFuture = tracker.make(sink.channel, who);
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("id");
    writer.writeInteger(oldFuture.id);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(oldFuture.channel);
    writer.writeObjectFieldIntro("array");
    writer.writeBoolean(array);
    writer.endObject();
    oldFuture.json = writer.toString();
    final var future = sink.dequeue(who);
    if (future.exists()) {
      oldFuture.take();
    }
    return future;
  }

  /** ask the user for one array of items, blocks entire universe */
  public SimpleFuture<T> fetchArray(final NtPrincipal who) {
    return fetch(who, true);
  }

  public SimpleFuture<NtMaybe<T>> fetchTimeoutItem(final NtPrincipal who, double timeout) {
    return fetchTimeout(who, false, timeout);
  }

  public SimpleFuture<NtMaybe<T>> fetchTimeout(final NtPrincipal who, boolean array, double timeout) {
    final var oldFuture = tracker.make(sink.channel, who);
    Timeout to = tracker.timeouts.create(oldFuture.id, timeout);
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("id");
    writer.writeInteger(oldFuture.id);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(oldFuture.channel);
    if (to != null) {
      writer.writeObjectFieldIntro("timeout");
      writer.beginObject();
      writer.writeObjectFieldIntro("started");
      writer.writeLong(to.timestamp);
      writer.writeObjectFieldIntro("seconds");
      writer.writeDouble(to.timeoutSeconds);
      writer.endObject();
    }
    writer.writeObjectFieldIntro("array");
    writer.writeBoolean(array);
    writer.endObject();
    oldFuture.json = writer.toString();

    // when does the timeout occur
    long limit = to.timestamp + (long) (to.timeoutSeconds * 1000);

    // we establish an exclusive timeline for the channel such that only the first message in the window belongs to this request
    final var future = sink.dequeueIf(who, limit);

    // it exists, so let's return it!
    if (future.exists()) {
      oldFuture.take();
      return new SimpleFuture<>(future.channel, future.who, new NtMaybe<>(future.await()));
    }

    // if the request is expired
    boolean expired = limit <= tracker.timeouts.time.get();
    if (expired) {
      // return an empty maybe to indicate a timeout
      return new SimpleFuture<>(future.channel, future.who, new NtMaybe<>());
    } else {
      // otherwise, let null indicates a future compute blocked
      return new SimpleFuture<>(future.channel, future.who, null);
    }
  }

  public SimpleFuture<NtMaybe<T>> fetchTimeoutArray(final NtPrincipal who, double timeout) {
    return fetchTimeout(who, true, timeout);
  }
}
