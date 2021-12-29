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

import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * This is a latch which has N outstanding tasks. Once all the tasks are complete, the supplier is
 * called, and the latch fires the callback. Any failures result in a failure of the lowest code.
 */
public class BulkLatch<T> {
  public final Executor executor;
  public final Callback<T> callback;

  public Supplier<T> supply;
  public int outstanding;
  private Integer errorCode;

  public BulkLatch(Executor executor, int outstanding, Callback<T> callback) {
    this.executor = executor;
    this.outstanding = outstanding;
    this.callback = callback;
    this.supply = null;
    this.errorCode = null;
  }

  /**
   * we have a split constructor since the latch is going to be defined prior to a complete
   * supplier. The LatchRefCallback is used before the with() call
   */
  public void with(Supplier<T> supply) {
    this.supply = supply;
  }

  /**
   * a service completed either successfully (newErrorCode == null) or not (newErrorCode != null)
   */
  public void countdown(Integer newErrorCode) {
    executor.execute(
        () -> {
          // something bad happened
          if (newErrorCode != null) {
            // absorb the error code
            if (errorCode == null) {
              errorCode = newErrorCode;
            } else {
              // if conflicts, pick the smallest error code
              if (newErrorCode < errorCode) {
                errorCode = newErrorCode;
              }
            }
          }
          outstanding--;
          if (outstanding == 0) {
            if (errorCode == null) {
              T value = supply.get();
              callback.success(value);
            } else {
              callback.failure(new ErrorCodeException(errorCode));
            }
          }
        });
  }
}
