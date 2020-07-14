/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import java.util.ArrayList;
import java.util.HashMap;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMaybe;

/** A sink is basically a queue for multiple users to contribute to. */
public class Sink<T> {
  /** a queue for a particular user */
  private class ClientChannelQueue {
    private final ArrayList<QueueItemWrapper> queue;

    private ClientChannelQueue() {
      this.queue = new ArrayList<>();
    }
  }
  /** a wrapper around an item with the associated task */
  private class QueueItemWrapper {
    private final T item;
    private final AsyncTask task;

    public QueueItemWrapper(final AsyncTask task, final T item) {
      this.item = item;
      this.task = task;
    }
  }

  /** the communication channel for the sink */
  public final String channel;

  /** the various queues per users */
  private final HashMap<NtClient, ClientChannelQueue> queues;

  /** construct the sink for the particular channel */
  public Sink(final String channel) {
    this.channel = channel;
    this.queues = new HashMap<>();
  }

  /** remove all items from the queue */
  public void clear() {
    queues.clear();
  }

  /** dequeue a message for a particular user; the future may not have a value */
  public SimpleFuture<T> dequeue(final NtClient who) {
    final var queue = queueFor(who);
    T value = null;
    if (queue.queue.size() > 0) {
      value = queue.queue.remove(0).item;
    }
    return new SimpleFuture<>(channel, who, value);
  }

  /** dequeue a message for a particular user; the future may not have a value */
  public SimpleFuture<NtMaybe<T>> dequeueMaybe(final NtClient who) {
    final var queue = queueFor(who);
    NtMaybe<T> value = null;
    if (queue.queue.size() > 0) {
      value = new NtMaybe<>(queue.queue.remove(0).item);
    }
    return new SimpleFuture<>(channel, who, value);
  }

  /** enqueue the given task and message; the task has the user in it */
  public void enqueue(final AsyncTask task, final T message) {
    queueFor(task.who).queue.add(new QueueItemWrapper(task, message));
  }

  /** get the queue for the particular user */
  private ClientChannelQueue queueFor(final NtClient value) {
    var queue = queues.get(value);
    if (queue == null) {
      /** create on-demand */
      queue = new ClientChannelQueue();
      queues.put(value, queue);
    }
    return queue;
  }
}
