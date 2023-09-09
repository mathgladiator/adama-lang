/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.mocks;

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
