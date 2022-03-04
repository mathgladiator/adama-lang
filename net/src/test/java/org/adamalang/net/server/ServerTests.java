/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.server;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClient;
import org.junit.Assert;
import org.junit.Test;

public class ServerTests {
  @Test
  public void ping() throws Exception {
    try (TestBed bed = new TestBed( 20000, "@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
      InstanceClient ic = new InstanceClient(bed.base, new ClientMetrics(new NoOpMetricsFactory()), null, "127.0.0.1:20000", bed.clientExecutor, null, null);
      try {
        Assert.assertFalse(ic.ping(2500));
        bed.startServer();
        Assert.assertTrue(ic.ping(15000));
      } finally {
        ic.close();
      }
    }
  }
}
