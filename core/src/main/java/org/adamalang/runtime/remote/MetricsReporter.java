package org.adamalang.runtime.remote;

import org.adamalang.runtime.data.Key;

/** simple interface to submit metrics too */
public interface MetricsReporter {
  public void emitMetrics(Key key, String metricsPayload);
}
