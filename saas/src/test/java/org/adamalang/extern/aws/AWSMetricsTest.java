package org.adamalang.extern.aws;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Test;

public class AWSMetricsTest {
  @Test
  public void coverage() {
    new AWSMetrics(new NoOpMetricsFactory());
  }
}
