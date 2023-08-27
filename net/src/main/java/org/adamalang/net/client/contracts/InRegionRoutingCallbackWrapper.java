/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;

/** Helper abstraction around RoutingCallback to reduce some duplicate code around failures */
@Deprecated
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
