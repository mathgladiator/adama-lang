/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.service.mocks.MockServiceBase;
import org.adamalang.web.service.mocks.MockWellKnownHandler;
import org.junit.Assert;
import org.junit.Test;

public class RedirectServiceRunnableTests {

  @Test
  public void test_interrupt() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Mock1);
    final var runnable = new RedirectAndWellknownServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), new MockWellKnownHandler(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(2500));
    Assert.assertTrue(runnable.isAccepting());
    thread.interrupt();
    thread.join();
    runnable.shutdown();
  }

  @Test
  public void test_shutdown() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Mock2);
    final var runnable = new RedirectAndWellknownServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), new MockWellKnownHandler(), () -> {});
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    runnable.shutdown();
    thread.join();
    runnable.shutdown();
  }

  @Test
  public void test_tight_shutdown() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Mock3);
    final var runnable = new RedirectAndWellknownServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), new MockWellKnownHandler(), () -> {});
    runnable.shutdown();
    final var thread = new Thread(runnable);
    thread.start();
    thread.join();
    runnable.shutdown();
  }
}
