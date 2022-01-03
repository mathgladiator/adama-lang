/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.contracts;

/** an unit of work that sits within a queue for processing */
public abstract class QueueAction<T> {
  private final int errorTimeout;
  private final int errorRejected;
  private boolean alive;

  public QueueAction(int errorTimeout, int errorRejected) {
    this.alive = true;
    this.errorTimeout = errorTimeout;
    this.errorRejected = errorRejected;
  }

  /** is the queue action alive */
  public boolean isAlive() {
    return alive;
  }

  /** execute the item if it is still valid */
  public void execute(T item) {
    if (alive) {
      executeNow(item);
    }
  }

  protected abstract void executeNow(T item);

  /** how the timeout is executed */
  public void killDueToTimeout() {
    alive = false;
    failure(errorTimeout);
  }

  protected abstract void failure(int code);

  public void killDueToReject() {
    alive = false;
    failure(errorRejected);
  }
}
