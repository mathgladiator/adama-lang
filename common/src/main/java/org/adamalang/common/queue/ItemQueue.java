/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.queue;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;

/** a queue of work for an item which may not be available */
public class ItemQueue<T> {
  private final SimpleExecutor executor;
  private final int bound;
  private final int timeout;
  private ArrayList<ItemAction<T>> buffer;
  private T item;

  public ItemQueue(SimpleExecutor executor, int bound, int timeout) {
    this.executor = executor;
    this.item = null;
    this.buffer = null;
    this.bound = bound;
    this.timeout = timeout;
  }

  public void ready(T item) {
    this.item = item;
    if (buffer != null) {
      for (ItemAction<T> action : buffer) {
        action.execute(item);
      }
      buffer = null;
    }
  }

  public void unready() {
    this.item = null;
  }

  public T nuke() {
    if (buffer != null) {
      for (ItemAction<T> action : buffer) {
        action.killDueToReject();
      }
      buffer = null;
    }
    T result = item;
    item = null;
    return result;
  }

  public void add(ItemAction<T> action) {
    add(action, timeout);
  }

  public void add(ItemAction<T> action, int customTimeout) {
    if (item != null) {
      action.execute(item);
      return;
    }
    if (buffer == null) {
      buffer = new ArrayList<>();
    }
    if (buffer.size() >= bound) {
      action.killDueToReject();
    } else {
      buffer.add(action);
      action.setCancelTimeout(executor.schedule(new NamedRunnable("expire-action") {
        @Override
        public void execute() throws Exception {
          action.killDueToTimeout();
          buffer.remove(action);
        }
      }, customTimeout));
    }
  }
}
