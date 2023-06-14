package org.adamalang.web.client.socket;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Test;

public class MultiWebClientRetryPoolMetricsTests {
  @Test
  public void trivial() {
    new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory());
  }
}
