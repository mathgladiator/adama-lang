/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.async;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

/** Ephemeral futures connect two ends together with a tiny state machine */
public class EphemeralFuture<T> {
  private boolean done;
  private boolean cancel;
  private T result;
  private Callback<T> callback;

  public EphemeralFuture() {
    this.done = false;
    this.cancel = false;
    this.result = null;
    this.callback = null;
  }

  /** send the future an object */
  public void send(T result) {
    boolean ship;
    synchronized (this) {
      this.result = result;
      ship = callback != null && result != null && !done;
      if (ship) {
        done = true;
      }
    }
    if (ship) {
      callback.success(result);
    }
  }

  /** attach a callback */
  public void attach(Callback<T> callback) {
    boolean ship;
    boolean kill = false;
    synchronized (this) {
      this.callback = callback;
      ship = callback != null && result != null && !done;
      if (cancel) {
        kill = true;
        ship = false;
      }
      if (ship) {
        done = true;
      }
    }
    if (ship) {
      callback.success(result);
    }
    if (kill) {
      kill();
    }
  }

  /** internal: send the failure out */
  private void kill() {
    callback.failure(new ErrorCodeException(ErrorCodes.TASK_CANCELLED));
  }

  /** cancel the future */
  public void cancel() {
    boolean kill;
    synchronized (this) {
      kill = callback != null && !done;
      done = true;
      cancel = true;
    }
    if (kill) {
      kill();
    }
  }
}
