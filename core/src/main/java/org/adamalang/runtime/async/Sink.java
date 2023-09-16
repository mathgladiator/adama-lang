/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.ArrayList;
import java.util.HashMap;

/** A sink is basically a queue for multiple users to contribute to. */
public class Sink<T> {
  /** the communication channel for the sink */
  public final String channel;
  /** the various queues per users */
  private final HashMap<NtPrincipal, ClientChannelQueue> queues;

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
  public SimpleFuture<T> dequeue(final NtPrincipal who) {
    final var queue = queueFor(who);
    T value = null;
    if (queue.queue.size() > 0) {
      value = queue.queue.remove(0).item;
    }
    return new SimpleFuture<>(channel, who, value);
  }

  /** get the queue for the particular user */
  private ClientChannelQueue queueFor(final NtPrincipal value) {
    var queue = queues.get(value);
    if (queue == null) {
      /** create on-demand */
      queue = new ClientChannelQueue();
      queues.put(value, queue);
    }
    return queue;
  }

  /** dequeue a message for a particular user against a timestamp limit; the future may not have a value */
  public SimpleFuture<T> dequeueIf(final NtPrincipal who, long timestampLimit) {
    final var queue = queueFor(who);
    T value = null;
    if (queue.queue.size() > 0) {
      ClientChannelQueuePair pair = queue.queue.get(0);
      if (pair.timestamp <= timestampLimit) {
        queue.queue.remove(0);
        value = pair.item;
      }
    }
    return new SimpleFuture<>(channel, who, value);
  }

  /** dequeue a message for a particular user; the future may not have a value */
  public SimpleFuture<NtMaybe<T>> dequeueMaybe(final NtPrincipal who) {
    final var queue = queueFor(who);
    NtMaybe<T> value = null;
    if (queue.queue.size() > 0) {
      value = new NtMaybe<>(queue.queue.remove(0).item);
    }
    return new SimpleFuture<>(channel, who, value);
  }

  /** enqueue the given task and message; the task has the user in it */
  public void enqueue(final AsyncTask task, final T message) {
    queueFor(task.who).queue.add(new ClientChannelQueuePair(message, task.timestamp));
  }

  private class ClientChannelQueuePair {
    public final T item;
    public final long timestamp;

    public ClientChannelQueuePair(T item, long timestamp) {
      this.item = item;
      this.timestamp = timestamp;
    }
  }

  /** a queue for a particular user */
  private class ClientChannelQueue {
    private final ArrayList<ClientChannelQueuePair> queue;

    private ClientChannelQueue() {
      this.queue = new ArrayList<>();
    }
  }
}
