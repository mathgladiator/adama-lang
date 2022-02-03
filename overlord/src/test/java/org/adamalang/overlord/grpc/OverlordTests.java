package org.adamalang.overlord.grpc;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.server.Server;
import org.adamalang.grpc.server.ServerMetrics;
import org.adamalang.grpc.server.ServerNexus;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.web.contracts.HttpHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
    OverlordClient client = new OverlordClient(identity, 10001, handler);
    client.setTarget(identity.ip + ":12312");
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    boolean gotNinjaOnce = false;
    try {
      server.put("/ninja", new HttpHandler.HttpResult("text/plain", "text".getBytes(StandardCharsets.UTF_8)));
      int attempts = 50;
      while (attempts-- > 0) {
        HttpHandler.HttpResult resultOfNinja = handler.handle("/ninja");
        HttpHandler.HttpResult resultOfNinja2 = handler.handle("/ninja2");
        HttpHandler.HttpResult heatResult = handler.handle("/heat");
        if (heatResult != null) {
          String htmlHeat = new String(heatResult.body);
          if (htmlHeat.contains("127.0.0.1:10001")) {
            System.err.println("heat was detected!");
            if (resultOfNinja2 != null) {
              System.err.println("second ninja got through and did the trick, huzzah!");
              return;
            }
          }
        }
        if (resultOfNinja != null) {
          if (!gotNinjaOnce) {
            gotNinjaOnce = true;
            System.err.println("first ninja document was discovered, sending second");
            server.put("/ninja2", new HttpHandler.HttpResult("text/plain", "text".getBytes(StandardCharsets.UTF_8)));
          }
        }
        Thread.sleep(250);
      }
      Assert.fail();
    } finally {
      client.shutdown();
      server.shutdown();
    }
  }

  public static String prefixForLocalhost() {
    for (String search : new String[] {"./", "../", "./grpc/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return candidate;
      }
    }
    throw new NullPointerException("could not find identity.localhost");
  }
}
