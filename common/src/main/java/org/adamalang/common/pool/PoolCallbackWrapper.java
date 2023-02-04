/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.pool;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

/** adapt an existing callback to register success/failure signals onto a PoolItem */
public class PoolCallbackWrapper<X, S> implements Callback<X> {
  private final Callback<X> wrapped;
  private final PoolItem<S> item;

  public PoolCallbackWrapper(Callback<X> wrapped, PoolItem<S> item) {
    this.wrapped = wrapped;
    this.item = item;
  }

  @Override
  public void success(X value) {
    try {
      wrapped.success(value);
    } finally {
      item.returnToPool();
    }
  }

  @Override
  public void failure(ErrorCodeException ex) {
    try {
      wrapped.failure(ex);
    } finally {
      item.signalFailure();
    }
  }
}
