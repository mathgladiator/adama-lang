/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.server;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Test;

public class HandlerTests {
  @Test
  public void trivial() {
    Handler handler = new Handler(new ServerNexus(null, null, null, new ServerMetrics(new NoOpMetricsFactory()), null, null, null, null, 1, 2), null);
    handler.request(-1);
    handler.create(-1);
  }
}
