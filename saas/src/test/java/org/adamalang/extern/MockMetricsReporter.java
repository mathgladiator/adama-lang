package org.adamalang.extern;

import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.remote.MetricsReporter;

import java.util.concurrent.ConcurrentHashMap;

public class MockMetricsReporter implements MetricsReporter {
  public final ConcurrentHashMap<Key, String> metrics;
  public MockMetricsReporter() {
    this.metrics = new ConcurrentHashMap<>();
  }

  @Override
  public void emitMetrics(Key key, String metricsPayload) {
    this.metrics.put(key, metricsPayload);
  }
}
