/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.async.OutstandingFutureTracker;
import org.adamalang.runtime.async.SimpleFuture;
import org.adamalang.runtime.async.Sink;
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
  public SimpleFuture<NtMaybe<T>> choose(final NtClient who, final NtMessageBase[] optionsRaw, final int limit) {
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
  public SimpleFuture<NtMaybe<T>> decide(final NtClient who, final NtMessageBase[] optionsRaw) {
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

  /** ask the user for one array of items, blocks entire universe */
  public SimpleFuture<T> fetchArray(final NtClient who) {
    return fetch(who, true);
  }

  /** ask the user for item/items */
  public SimpleFuture<T> fetch(final NtClient who, boolean array) {
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

  /** ask the user for one item, blocks entire universe */
  public SimpleFuture<T> fetchItem(final NtClient who) {
    return fetch(who, false);
  }
}
