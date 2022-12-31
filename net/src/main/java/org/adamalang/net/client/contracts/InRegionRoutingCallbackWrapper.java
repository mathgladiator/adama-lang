/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;

/** Helper abstraction around RoutingCallback to reduce some duplicate code around failures */
public abstract class InRegionRoutingCallbackWrapper<T> implements RoutingCallback {
  private final RequestResponseMonitor.RequestResponseMonitorInstance mInstance;
  private final int errorCodeOnRegion;
  public final Callback<T> callback;

  public InRegionRoutingCallbackWrapper(RequestResponseMonitor.RequestResponseMonitorInstance mInstance, Callback<T> callback, int errorCodeOnRegion) {
    this.mInstance = mInstance;
    this.callback = callback;
    this.errorCodeOnRegion = errorCodeOnRegion;
  }

  @Override
  public void onRegion(String region) {
    failure(new ErrorCodeException(errorCodeOnRegion));
  }

  @Override
  public void failure(ErrorCodeException ex) {
    mInstance.failure(ex.code);
    callback.failure(ex);
  }
}
