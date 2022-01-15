/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.client.ClientMetrics;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.mocks.MockClentLifecycle;
import org.adamalang.grpc.mocks.StdErrLogger;
import org.junit.Test;

public class EndToEnd_HappyTests {
  @Test
  public void ss() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed bed =
        new TestBed(
            20000,
            "@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
      MockClentLifecycle lifecycle = new MockClentLifecycle();
      InstanceClient instanceClient = new InstanceClient(bed.identity, metrics, null, "127.0.0.1:20000", bed.clientExecutor, lifecycle, new StdErrLogger());
    }
  }
}
