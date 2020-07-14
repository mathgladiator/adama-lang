/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.async.OutstandingFutureTracker;
import org.adamalang.runtime.async.SimpleFuture;
import org.adamalang.runtime.async.Sink;
import org.adamalang.runtime.contracts.CanConvertToObject;
import org.adamalang.runtime.stdlib.Utility;

/** a channel */
public class NtChannel<T> {
  public final Sink<T> sink;
  public final OutstandingFutureTracker tracker;

  public NtChannel(final OutstandingFutureTracker tracker, final Sink<T> sink) {
    this.tracker = tracker;
    this.sink = sink;
  }

  /** from a list of options, choose $limit of them */
  public SimpleFuture<NtMaybe<T>> choose(final NtClient who, final CanConvertToObject[] optionsRaw, final int limit) {
    final var actualLimit = Math.min(limit, optionsRaw.length);
    if (actualLimit == 0) { return new SimpleFuture<>(sink.channel, who, new NtMaybe<>()); }
    final var options = Utility.createArrayNode();
    for (final CanConvertToObject obj : optionsRaw) {
      options.add(obj.convertToObjectNode());
    }
    final var oldFuture = tracker.make(sink.channel, who, options, actualLimit, actualLimit, true);
    final var future = sink.dequeueMaybe(who);
    if (future.exists()) {
      oldFuture.take();
    }
    return future;
  }

  /** from a list of options, pick one of them */
  public SimpleFuture<NtMaybe<T>> decide(final NtClient who, final CanConvertToObject[] optionsRaw) {
    if (optionsRaw.length == 0) { return new SimpleFuture<>(sink.channel, who, new NtMaybe<>()); }
    final var options = Utility.createArrayNode();
    for (final CanConvertToObject obj : optionsRaw) {
      options.add(obj.convertToObjectNode());
    }
    final var oldFuture = tracker.make(sink.channel, who, options, 1, 1, true);
    final var future = sink.dequeueMaybe(who);
    if (future.exists()) {
      oldFuture.take();
    }
    return future;
  }

  /** ask the user for one item, blocks entire universe */
  public SimpleFuture<T> fetch(final NtClient who) {
    final var oldFuture = tracker.make(sink.channel, who, null, 1, 1, true);
    final var future = sink.dequeue(who);
    if (future.exists()) {
      oldFuture.take();
    }
    return future;
  }
}
