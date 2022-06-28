/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.contracts.MeteringStream;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.net.mocks.MockMeteringFlow;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebPutRaw;
import org.adamalang.runtime.sys.web.WebResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ClientTests {
  @Test
  public void simple_happy_flows() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12500,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      try {
        waitForRouting(bed, client);
        CountDownLatch latchGetDeployTargets = new CountDownLatch(1);
        client.getDeploymentTargets(
            "space",
            new Consumer<String>() {
              @Override
              public void accept(String s) {
                Assert.assertEquals("127.0.0.1:12500", s);
                latchGetDeployTargets.countDown();
              }
            });
        Assert.assertTrue(latchGetDeployTargets.await(5000, TimeUnit.MILLISECONDS));
        client.notifyDeployment("127.0.0.1:12500", "space");
        CountDownLatch latchRandomBillingExchangeFinishes = new CountDownLatch(1);

        CountDownLatch latchFound = new CountDownLatch(1);
        AtomicReference<Boolean> got = new AtomicReference<>(null);
        client.waitForCapacity("space", 5000, new Consumer<Boolean>() {
          @Override
          public void accept(Boolean b) {
            got.set(b);
            latchFound.countDown();
          }
        });
        Assert.assertTrue(latchFound.await(7500, TimeUnit.MILLISECONDS));
        Assert.assertTrue(got.get());
        client.randomMeteringExchange(new MeteringStream() {
          @Override
          public void handle(String target, String batch, Runnable after) {
            after.run();
          }

          @Override
          public void failure(int code) {

          }

          @Override
          public void finished() {
            latchRandomBillingExchangeFinishes.countDown();
          }
        });
        Assert.assertTrue(latchGetDeployTargets.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch latchCreatedKey = new CountDownLatch(1);
        client.create("127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            latchCreatedKey.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("CODE:" + ex.code);
          }
        });


        Assert.assertTrue(latchCreatedKey.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch latchGotConnected = new CountDownLatch(1);
        CountDownLatch latchGotData = new CountDownLatch(1);
        CountDownLatch latchGotDisconnect = new CountDownLatch(1);
        Connection connection = client.connect("127.0.0.1", "origin", "me", "dev", "space", "key1", "{}", null, new SimpleEvents() {
          @Override
          public void connected() {
            latchGotConnected.countDown();
          }

          @Override
          public void delta(String data) {
            latchGotData.countDown();
          }

          @Override
          public void error(int code) {
          }

          @Override
          public void disconnected() {
            latchGotDisconnect.countDown();
          }
        });
        Assert.assertTrue(latchGotConnected.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latchGotData.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch latchGotReflection = new CountDownLatch(1);
        CountDownLatch latchFailedOnReflectionBadSpace = new CountDownLatch(1);
        client.reflect("space", "key", new Callback<String>() {
          @Override
          public void success(String value) {
            latchGotReflection.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        client.reflect("nope", "key", new Callback<String>() {
          @Override
          public void success(String value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("EX:" + ex.code);
            Assert.assertEquals(753724, ex.code);
            latchFailedOnReflectionBadSpace.countDown();
          }
        });
        Assert.assertTrue(latchGotReflection.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latchFailedOnReflectionBadSpace.await(5000, TimeUnit.MILLISECONDS));
        connection.close();
        Assert.assertTrue(latchGotDisconnect.await(5000, TimeUnit.MILLISECONDS));
      } finally{
        client.shutdown();
      }
    }
  }

  @Test
  public void web() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12700,
                 "@static { create { return true; } } @web get / { return {html:\"root\"};\n" + "}  message M { int x; } public int z = 1000; @web put / (M m) { z = m.x; return {html:\"c:\" + z}; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      try {
        waitForRouting(bed, client);
        CountDownLatch latchGetDeployTargets = new CountDownLatch(1);
        client.getDeploymentTargets(
            "space",
            new Consumer<String>() {
              @Override
              public void accept(String s) {
                Assert.assertEquals("127.0.0.1:12700", s);
                latchGetDeployTargets.countDown();
              }
            });
        Assert.assertTrue(latchGetDeployTargets.await(5000, TimeUnit.MILLISECONDS));
        client.notifyDeployment("127.0.0.1:12700", "space");

        CountDownLatch latchFound = new CountDownLatch(1);
        AtomicReference<Boolean> got = new AtomicReference<>(null);
        client.waitForCapacity("space", 5000, new Consumer<Boolean>() {
          @Override
          public void accept(Boolean b) {
            got.set(b);
            latchFound.countDown();
          }
        });
        Assert.assertTrue(latchFound.await(7500, TimeUnit.MILLISECONDS));
        Assert.assertTrue(got.get());
        Assert.assertTrue(latchGetDeployTargets.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch latchCreatedKey = new CountDownLatch(1);
        client.create("127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            latchCreatedKey.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("CODE:" + ex.code);
          }
        });
        Assert.assertTrue(latchCreatedKey.await(5000, TimeUnit.MILLISECONDS));

        CountDownLatch getLatches = new CountDownLatch(2);
        client.webGet("space", "key1", new WebGet(NtClient.NO_ONE, "/", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            Assert.assertEquals("root", value.body);
            getLatches.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("Ex1:" + ex.code);

          }
        });
        TreeMap<String, String> header1 = new TreeMap<>();
        header1.put("x", "y");
        client.webGet("space", "key1", new WebGet(NtClient.NO_ONE, "/nope", header1, new NtDynamic("{}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            getLatches.countDown();
            Assert.assertEquals(133308, ex.code);
          }
        });

        CountDownLatch putLatches = new CountDownLatch(2);
        client.webPut("space", "key1", new WebPut(NtClient.NO_ONE, new WebPutRaw("/", new TreeMap<>(), new NtDynamic("{}"), "{\"x\":123}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            Assert.assertEquals("c:123", value.body);
            putLatches.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });

        client.webPut("space", "key1", new WebPut(NtClient.NO_ONE, new WebPutRaw("/nope", new TreeMap<>(), new NtDynamic("{}"), "{\"x\":123}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            System.err.println(value.body);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            putLatches.countDown();
          }
        });

        Assert.assertTrue(getLatches.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(putLatches.await(5000, TimeUnit.MILLISECONDS));
      } finally{
        client.shutdown();
      }
    }
  }

  @Test
  public void metering_happy() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12501,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      try {
        waitForRouting(bed, client);
        {
          MockMeteringFlow mock = new MockMeteringFlow();
          Runnable latch = mock.latchAt(1);
          client.randomMeteringExchange(mock);
          latch.run();
          mock.assertWrite(0, "FINISHED");
        }

        CountDownLatch finished = new CountDownLatch(1);
        bed.batchMaker.write(new MeterReading(0, System.currentTimeMillis(), "space", "hash", new PredictiveInventory.MeteringSample(0, 1, 2, 3, 4)));
        bed.batchMaker.flush(finished);
        Assert.assertTrue(finished.await(5000, TimeUnit.MILLISECONDS));
        String id = bed.batchMaker.getNextAvailableBatchId();
        Assert.assertNotNull(id);


        {
          MockMeteringFlow mock = new MockMeteringFlow();
          Runnable latch = mock.latchAt(2);
          client.randomMeteringExchange(mock);
          latch.run();
          mock.assertWrite(0, "HANDLE!");
          mock.assertWrite(1, "FINISHED");
        }


      } finally{
        client.shutdown();
      }
    }
  }

  @Test
  public void no_capacity() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12502,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      try {
        CountDownLatch latch1Failed = new CountDownLatch(1);
        client.notifyDeployment("127.0.0.1:" + bed.port, "space");
        client.create("127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            System.err.println("Success");
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("L1:" + ex.code);
            Assert.assertEquals(753724, ex.code);
            latch1Failed.countDown();
          }
        });
        CountDownLatch latch2Failed = new CountDownLatch(1);
        client.randomMeteringExchange(new MeteringStream() {
          @Override
          public void handle(String target, String batch, Runnable after) {
          }

          @Override
          public void failure(int code) {
            System.err.println("L2:" + code);
            Assert.assertEquals(753724, code);
            latch2Failed.countDown();
          }

          @Override
          public void finished() {
          }
        });
        CountDownLatch latch3Failed = new CountDownLatch(1);
        client.reflect("x", "y", new Callback<String>() {
          @Override
          public void success(String value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("L3:" + ex.code);
            Assert.assertEquals(753724, ex.code);
            latch3Failed.countDown();
          }
        });
        CountDownLatch latch4 = new CountDownLatch(1);
        AtomicReference<Boolean> got = new AtomicReference<>(null);
        client.waitForCapacity("xyz", 500, new Consumer<Boolean>() {
          @Override
          public void accept(Boolean b) {
            got.set(b);
            latch4.countDown();
          }
        });
        Assert.assertTrue(latch1Failed.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2Failed.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch3Failed.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch4.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertFalse(got.get());
      } finally{
        client.shutdown();
      }
    }
  }

  @Test
  public void not_allowed_create() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12503,
                 "@static { create { return false; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      try {
        waitForRouting(bed, client);
        CountDownLatch latchFailed = new CountDownLatch(1);
        client.create("127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.assertEquals(134259, ex.code);
            latchFailed.countDown();
          }
        });
        Assert.assertTrue(latchFailed.await(5000, TimeUnit.MILLISECONDS));
      } finally{
        client.shutdown();
      }
    }
  }

  public static void waitForRouting(TestBed bed, Client client) throws InterruptedException {
    client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:" + bed.port));
    CountDownLatch latchFound = new CountDownLatch(1);
    for (int k = 0; k < 10; k++) {
      client.routing().get(new Key("space", "key"), new RoutingSubscriber() {
        @Override
        public void onRegion(String region) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
        }

        @Override
        public void onMachine(String machine) {
          if (machine != null) {
            latchFound.countDown();
          }
        }
      });
      if (latchFound.await(1500, TimeUnit.MILLISECONDS)) {
        break;
      }
    }
  }

  @Test
  public void bad_server() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12504,
                 "@static { create { return false; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.naughty().inventory("space").failEverything().start();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      waitForRouting(bed, client);
      CountDownLatch failures = new CountDownLatch(5);
      client.notifyDeployment("127.0.0.1:" + bed.port, "*");
      client.notifyDeployment("127.0.0.1:" + (bed.port + 1), "*");
      client.randomMeteringExchange(
          new MeteringStream() {
            @Override
            public void handle(String target, String batch, Runnable after) {
              after.run();
            }

            @Override
            public void failure(int code) {
              System.err.println("RME:" + code);
              Assert.assertEquals(123456789, code);
              failures.countDown();
            }

            @Override
            public void finished() {}
          });
      client.reflect("x", "y", new Callback<String>() {
        @Override
        public void success(String value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("R1:" + ex.code);
          Assert.assertEquals(753724, ex.code);
          failures.countDown();
        }
      });
      client.reflect("space", "y", new Callback<String>() {
        @Override
        public void success(String value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("R2:" + ex.code);
          Assert.assertEquals(123456789, ex.code);
          failures.countDown();
        }
      });
      client.connect("127.0.0.1", "origin", "agent", "auth", "space", "key", "{}", null, new SimpleEvents() {
        @Override
        public void connected() {

        }

        @Override
        public void delta(String data) {

        }

        @Override
        public void error(int code) {
          System.err.println("CONN:" + code);
          Assert.assertEquals(123456789, code);
          failures.countDown();
        }

        @Override
        public void disconnected() {

        }
      });
      client.create("127.0.0.1", "origin", "agent", "au", "space", "key", null, "{}", new Callback<Void>() {
        @Override
        public void success(Void value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("CR:" + ex.code);
          Assert.assertEquals(123456789, ex.code);
          failures.countDown();
        }
      });
      Assert.assertTrue(failures.await(5000, TimeUnit.MILLISECONDS));
    }
  }

  @Test
  public void fakes() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12505,
                 "@static { create { return false; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.naughty().inventory("space").start();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      waitForRouting(bed, client);
      client.notifyDeployment("127.0.0.1:12505", "*");
      CountDownLatch meteringLatch = new CountDownLatch(2);
      client.randomMeteringExchange(
          new MeteringStream() {
            @Override
            public void handle(String target, String batch, Runnable after) {
              if ("fake-batch".equals(batch)) {
                meteringLatch.countDown();
              }
              after.run();
            }

            @Override
            public void failure(int code) {
            }

            @Override
            public void finished() {
              meteringLatch.countDown();
            }
          });
      Assert.assertTrue(meteringLatch.await(2000, TimeUnit.MILLISECONDS));
    }
  }

  @Test
  public void shut_server() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12506,
                 "@static { create { return false; } invent { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.naughty().inventory("space").closeStream().start();
      ClientConfig clientConfig = new TestClientConfig();
      Client client = new Client(bed.base, clientConfig, new ClientMetrics(new NoOpMetricsFactory()), ClientRouter.REACTIVE(new ClientMetrics(new NoOpMetricsFactory())), null);
      waitForRouting(bed, client);
      CountDownLatch closures = new CountDownLatch(1);
      client.connect("127.0.0.1", "origin", "agent", "auth", "space", "key", "{}", null, new SimpleEvents() {
        @Override
        public void connected() {
          System.err.println("connected!");
        }

        @Override
        public void delta(String data) {
          System.err.println("data:" + data);
        }

        @Override
        public void error(int code) {
          System.err.println("error:" + code);
          Assert.assertEquals(947263, code);
          closures.countDown();
        }

        @Override
        public void disconnected() {
          System.err.println("disconnected");
        }
      });
      long started = System.currentTimeMillis();
      Assert.assertTrue(closures.await(15000, TimeUnit.MILLISECONDS));
      System.err.println("TOOK:" + (System.currentTimeMillis() - started));
    }
  }
}
