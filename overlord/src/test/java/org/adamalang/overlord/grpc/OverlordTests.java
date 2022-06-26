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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.web.contracts.HttpHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<HttpHandler.HttpResult> resultOfNinjaRef = new AtomicReference<>();
        AtomicReference<HttpHandler.HttpResult> resultOfNinja2Ref = new AtomicReference<>();
        AtomicReference<HttpHandler.HttpResult> heatResultRef = new AtomicReference<>();
        CountDownLatch latchGet = new CountDownLatch(3);
        handler.handleGet("/ninja", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
          @Override
          public void success(HttpHandler.HttpResult value) {
            resultOfNinjaRef.set(value);
            latchGet.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        handler.handleGet("/ninja2", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
          @Override
          public void success(HttpHandler.HttpResult value) {
            resultOfNinja2Ref.set(value);
            latchGet.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        handler.handleGet("/heat", new TreeMap<>(), "{}", new Callback<HttpHandler.HttpResult>() {
          @Override
          public void success(HttpHandler.HttpResult value) {
            heatResultRef.set(value);
            latchGet.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        HttpHandler.HttpResult resultOfNinja = resultOfNinjaRef.get();
        HttpHandler.HttpResult resultOfNinja2 = resultOfNinja2Ref.get();
        HttpHandler.HttpResult heatResult = heatResultRef.get();
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
