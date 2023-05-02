/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.metrics;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

/** monitor a callback */
public abstract class CallbackMonitor {

  public <T> Callback<T> wrap(Callback<T> callback) {
    CallbackMonitorInstance instance = start();
    return new Callback<T>() {
      @Override
      public void success(T value) {
        instance.success();
        callback.success(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        instance.failure(ex.code);
        callback.failure(ex);
      }
    };
  }

  public abstract CallbackMonitorInstance start();

  public interface CallbackMonitorInstance {
    void success();

    void failure(int code);
  }
}
