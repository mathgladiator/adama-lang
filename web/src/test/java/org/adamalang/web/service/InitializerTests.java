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
import org.adamalang.web.service.mocks.NullCertificateFinder;
import org.junit.Test;

public class InitializerTests {
  @Test
  public void sanity() throws Exception {
    new Initializer(WebConfigTests.mockConfig(WebConfigTests.Scenario.Dev), new WebMetrics(new NoOpMetricsFactory()), new MockServiceBase(), new NullCertificateFinder(), null, null, null);
  }
}
