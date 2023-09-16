/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
