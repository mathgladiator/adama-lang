package org.adamalang.web.client.socket;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for maintaining a connection to a websocket endpoint */
public class MultiWebClientRetryPoolMetrics {
  public final ItemActionMonitor queue;
  public final Runnable disconnected;
  public final Runnable failure;
  public final Runnable slow;
  public final Inflight inflight;

  public MultiWebClientRetryPoolMetrics(MetricsFactory factory) {
    queue = factory.makeItemActionMonitor("mwcr_pool_queue");
    disconnected = factory.counter("mwcr_pool_disconnected");
    failure = factory.counter("mwcr_pool_failure");
    slow = factory.counter("mwcr_pool_slow");
    inflight = factory.inflight("mwcr_pool_inflight");
  }
}
