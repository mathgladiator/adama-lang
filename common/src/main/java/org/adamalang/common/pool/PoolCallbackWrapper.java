/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
