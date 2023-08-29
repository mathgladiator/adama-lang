/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.region;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.StreamMonitor;

public class AdamaDeploymentSyncMetrics {
  public final CallbackMonitor adamasync_connected;
  public final Inflight adamasync_watching;
  public final StreamMonitor adamasync_streaming_update;

  public AdamaDeploymentSyncMetrics(MetricsFactory factory) {
    this.adamasync_connected = factory.makeCallbackMonitor("adamasync_connected");
    this.adamasync_watching = factory.inflight("adamasync_watching");
    this.adamasync_streaming_update = factory.makeStreamMonitor("adamasync_streaming_update");
  }
}
