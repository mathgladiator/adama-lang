/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

/** a latch for pumping a callback */
public class MultiVoidCallbackLatch {
  private final Callback<Void> callback;
  private int remaining;
  private final int errorCode;
  private boolean failed;

  public MultiVoidCallbackLatch(Callback<Void> callback, int remaining, int errorCode) {
    this.callback = callback;
    this.remaining = remaining;
    this.errorCode = errorCode;
    this.failed = false;
  }

  private synchronized boolean atomicCuccess() {
    if (failed) {
      return false;
    }
    remaining--;
    return remaining == 0;
  }

  public void success() {
    if (atomicCuccess()) {
      callback.success(null);
    }
  }

  private synchronized boolean atomicFailure() {
    if (failed) {
      return false;
    }
    failed = true;
    return true;
  }

  public void failure() {
    if (atomicFailure()) {
      callback.failure(new ErrorCodeException(errorCode));
    }
  }
}
