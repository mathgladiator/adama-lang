/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.server;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Test;

public class HandlerTests {
  @Test
  public void trivial() {
    Handler handler = new Handler(new ServerNexus(null, null, null, new ServerMetrics(new NoOpMetricsFactory()), null, null, null, null, null, 1, 2), null);
    handler.request(-1);
    handler.create(-1);
  }
}
