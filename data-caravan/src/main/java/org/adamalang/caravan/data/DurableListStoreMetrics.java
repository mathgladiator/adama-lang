package org.adamalang.caravan.data;

import org.adamalang.common.metrics.MetricsFactory;

public class DurableListStoreMetrics {
  public final Runnable flush;

  public DurableListStoreMetrics(MetricsFactory factory) {
    this.flush = factory.counter("dls_flush");
  }
}
