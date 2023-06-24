/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.io;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

/**
 * this wraps a callback to call into a BulkLatch. This acts as a ref which is triggers a cascade on
 * the bulk latch
 */
public class LatchRefCallback<T> implements Callback<T> {
  public final BulkLatch<?> latch;
  private T value;

  public LatchRefCallback(BulkLatch<?> latch) {
    this.latch = latch;
    this.value = null;
  }

  /** get the value; this is only available after a success */
  public T get() {
    return value;
  }

  @Override
  public void success(T value) {
    this.value = value;
    latch.countdown(null);
  }

  @Override
  public void failure(ErrorCodeException ex) {
    latch.countdown(ex.code);
  }
}
