/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.grpc;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class OverlordTests {
  @Test
  public void integ() throws Exception {
    MachineIdentity identity = MachineIdentity.fromFile(prefixForLocalhost());
    ConcurrentCachedHttpHandler handler = new ConcurrentCachedHttpHandler();
    HeatTable table = new HeatTable(handler);
    AtomicInteger heartbeats = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(2);
    OverlordServer server = new OverlordServer(identity, 12312, table, new OverlordMetrics(new NoOpMetricsFactory()), () -> {
      heartbeats.incrementAndGet();
      latch.countDown();
    });
    OverlordClient client = new OverlordClient(identity, 10001);
    client.setTarget(identity.ip + ":12312");
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    try {
    } finally {
      client.shutdown();
      server.shutdown();
    }
  }

  public static String prefixForLocalhost() {
    for (String search : new String[] {"./", "../", "./overlord/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return candidate;
      }
    }
    throw new NullPointerException("could not find identity.localhost");
  }
}
