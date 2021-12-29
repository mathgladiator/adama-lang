/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
