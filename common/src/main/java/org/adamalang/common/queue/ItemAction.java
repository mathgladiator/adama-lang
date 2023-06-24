/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
