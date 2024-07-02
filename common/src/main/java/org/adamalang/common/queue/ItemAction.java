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

import org.adamalang.common.metrics.ItemActionMonitor;

/** an unit of work that sits within a queue for processing */
public abstract class ItemAction<T> {
  private static final Runnable DEFAULT_CANCEL_TIMEOUT = () -> {
  };
  private final int errorTimeout;
  private final int errorRejected;
  private final ItemActionMonitor.ItemActionMonitorInstance monitor;
  private boolean alive;
  private Runnable cancelTimeout;

  public ItemAction(int errorTimeout, int errorRejected, ItemActionMonitor.ItemActionMonitorInstance monitor) {
    this.alive = true;
    this.errorTimeout = errorTimeout;
    this.errorRejected = errorRejected;
    this.monitor = monitor;
    this.cancelTimeout = DEFAULT_CANCEL_TIMEOUT;
  }

  /** is the queue action alive */
  public boolean isAlive() {
    return alive;
  }

  /** execute the item if it is still valid */
  public void execute(T item) {
    if (alive) {
      executeNow(item);
      alive = false;
      cancelTimeout.run();
      monitor.executed();
    }
  }

  protected abstract void executeNow(T item);

  /** how the timeout is executed */
  public void killDueToTimeout() {
    if (alive) {
      alive = false;
      failure(errorTimeout);
      monitor.timeout();
    }
  }

  protected abstract void failure(int code);

  public void killDueToReject() {
    if (alive) {
      alive = false;
      failure(errorRejected);
      monitor.rejected();
      cancelTimeout.run();
    }
  }

  public void setCancelTimeout(Runnable cancelTimeout) {
    this.cancelTimeout = cancelTimeout;
  }
}
