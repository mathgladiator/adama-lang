/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
