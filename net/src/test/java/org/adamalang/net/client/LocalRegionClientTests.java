/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.net.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.ConnectionMode;
import org.adamalang.runtime.sys.web.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LocalRegionClientTests {
  private static final WebContext CONTEXT = new WebContext(NtPrincipal.NO_ONE, "ORIGIN", "1.2.3.4");

  @Test
  public void simple_happy_flows() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12500,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
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
            }, 3);
        Assert.assertTrue(latchGetDeployTargets.await(5000, TimeUnit.MILLISECONDS));
        client.notifyDeployment("127.0.0.1:12500", "space");
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
        client.create("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
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
        Connection connection = client. connect("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", "{}", ConnectionMode.Full, new SimpleEvents() {
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
            System.err.println("ERROR:" + code);
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
        client.reflect("127.0.0.1:" + bed.port, "space", "key", new Callback<String>() {
          @Override
          public void success(String value) {
            latchGotReflection.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        client.reflect("127.0.0.1:" + bed.port, "nope", "key", new Callback<String>() {
          @Override
          public void success(String value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("EX:" + ex.code);
            Assert.assertEquals(134214, ex.code);
            latchFailedOnReflectionBadSpace.countDown();
          }
        });

        Assert.assertTrue(latchGotReflection.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latchFailedOnReflectionBadSpace.await(5000, TimeUnit.MILLISECONDS));

        CountDownLatch latchFinder = new CountDownLatch(1);
        client.finder.find(new Key("some-space", "some-key"), new Callback<DocumentLocation>() {
          @Override
          public void success(DocumentLocation location) {
            System.out.println(location.region + "/" + location.machine);
            latchFinder.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("Failed to find!");
            System.err.println(ex.code);
          }
        });
        Assert.assertTrue(latchFinder.await(5000, TimeUnit.MILLISECONDS));

        connection.close();
        Assert.assertTrue(latchGotDisconnect.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch deleteByOverlord = new CountDownLatch(1);
        client.delete("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "overlord", "space", "key1", new Callback<Void>() {
          @Override
          public void success(Void value) {
            deleteByOverlord.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(deleteByOverlord.await(5000, TimeUnit.MILLISECONDS));
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
                 "@static { create { return true; } }" +
                     "@web get / { return {html:\"root\"};\n" + "} " +
                     "@web options /moop { return {cors:true};\n" + "} " +
                     "@web get /cors { return {html:\"my-cors\", cors:true, cache_ttl_seconds:42};\n" + "} " +
                     "@web delete /deldel { return {html:\"deleted\", cors:true, cache_ttl_seconds:42};\n" + "} " +
                     "message M { int x; } public int z = 1000; @web put / (M m) { z = m.x; return {html:\"c:\" + z}; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
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
            }, 3);
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
        client.create("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
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

        CountDownLatch getLatches = new CountDownLatch(3);
        client.webGet("127.0.0.1:" + bed.port, "space", "key1", new WebGet(CONTEXT, "/", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
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
        client.webGet("127.0.0.1:" + bed.port, "space", "key1", new WebGet(CONTEXT, "/cors", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            Assert.assertEquals("my-cors", value.body);
            Assert.assertTrue(value.cors);
            Assert.assertEquals(42, value.cache_ttl_seconds);
            getLatches.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("Ex1:" + ex.code);

          }
        });
        TreeMap<String, String> header1 = new TreeMap<>();
        header1.put("x", "y");
        client.webGet("127.0.0.1:" + bed.port, "space", "key1", new WebGet(CONTEXT, "/nope", header1, new NtDynamic("{}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            getLatches.countDown();
            Assert.assertEquals(133308, ex.code);
          }
        });

        CountDownLatch putLatches = new CountDownLatch(4);
        client.webPut("127.0.0.1:" + bed.port, "space", "key1", new WebPut(CONTEXT, "/", new TreeMap<>(), new NtDynamic("{}"), "{\"x\":123}"), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            Assert.assertEquals("c:123", value.body);
            putLatches.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });

        client.webPut("127.0.0.1:" + bed.port, "space", "key1", new WebPut(CONTEXT, "/nope", new TreeMap<>(), new NtDynamic("{}"), "{\"x\":123}"), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            System.err.println(value.body);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            putLatches.countDown();
          }
        });

        client.webDelete("127.0.0.1:" + bed.port, "space", "key1", new WebDelete(CONTEXT, "/deldel", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            System.err.println(value.body);
            Assert.assertEquals("deleted", value.body);
            putLatches.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });

        client.webOptions("127.0.0.1:" + bed.port, "space", "key1", new WebGet(CONTEXT, "/moop", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
          @Override
          public void success(WebResponse value) {
            Assert.assertTrue(value.cors);
            putLatches.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
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
  public void no_capacity() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12502,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      ClientConfig clientConfig = new TestClientConfig();
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      try {
        CountDownLatch latch1Failed = new CountDownLatch(1);
        client.notifyDeployment("127.0.0.1:" + bed.port, "space");
        client.create("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            System.err.println("Success");
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("L1:" + ex.code);
            Assert.assertEquals(719932, ex.code);
            latch1Failed.countDown();
          }
        });
        CountDownLatch latch3Failed = new CountDownLatch(1);
        client.reflect("127.0.0.1:" + bed.port, "x", "y", new Callback<String>() {
          @Override
          public void success(String value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("L3:" + ex.code);
            Assert.assertEquals(719932, ex.code);
            latch3Failed.countDown();
          }
        });

        CountDownLatch deleteByOverlord = new CountDownLatch(1);
        client.delete("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "overlord", "space", "key1", new Callback<Void>() {
          @Override
          public void success(Void value) {
            System.err.println("Successful delete?");
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("DEx:" + ex.code);
            Assert.assertEquals(719932, ex.code);
            deleteByOverlord.countDown();
          }
        });
        Assert.assertTrue(deleteByOverlord.await(5000, TimeUnit.MILLISECONDS));

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
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      try {
        waitForRouting(bed, client);
        CountDownLatch latchFailed = new CountDownLatch(1);
        client.create("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
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

  @Test
  public void not_allowed_delete() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12503,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      ClientConfig clientConfig = new TestClientConfig();
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      try {
        waitForRouting(bed, client);
        CountDownLatch created = new CountDownLatch(1);
        client.create("127.0.0.1:" + bed.port,  "127.0.0.1", "origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            created.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        Assert.assertTrue(created.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch deleted = new CountDownLatch(1);
        client.delete("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", new Callback<Void>() {
          @Override
          public void success(Void value) {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.assertEquals(147186, ex.code);
            deleted.countDown();
          }
        });
        Assert.assertTrue(deleted.await(5000, TimeUnit.MILLISECONDS));
      } finally{
        client.shutdown();
      }
    }
  }

  public static void waitForRouting(TestBed bed, LocalRegionClient client) throws InterruptedException {
    client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:" + bed.port));
    CountDownLatch latchFound = new CountDownLatch(1);
    for (int k = 0; k < 10; k++) {
      client.getMachineFor(new Key("space", "key"), new Callback<>() {
        @Override
        public void success(String value) {
          latchFound.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

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
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      waitForRouting(bed, client);
      CountDownLatch failures = new CountDownLatch(4);
      client.notifyDeployment("127.0.0.1:" + bed.port, "*");
      client.notifyDeployment("127.0.0.1:" + (bed.port + 1), "*");
      client.reflect("127.0.0.1:" + bed.port, "x", "y", new Callback<String>() {
        @Override
        public void success(String value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("R1:" + ex.code);
          Assert.assertEquals(123456789, ex.code);
          failures.countDown();
        }
      });
      client.reflect("127.0.0.1:" + bed.port, "space", "y", new Callback<String>() {
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
      CountDownLatch deleted = new CountDownLatch(1);
      client.delete("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "me", "dev", "space", "key1", new Callback<Void>() {
        @Override
        public void success(Void value) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("CD2:" + ex.code);
          Assert.assertEquals(123456789, ex.code);
          deleted.countDown();
        }
      });
      Assert.assertTrue(deleted.await(5000, TimeUnit.MILLISECONDS));
      client.connect("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "agent", "auth", "space", "key", "{}", ConnectionMode.Full, new SimpleEvents() {
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
      client.create("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "agent", "au", "space", "key", null, "{}", new Callback<Void>() {
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
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      waitForRouting(bed, client);
      client.notifyDeployment("127.0.0.1:12505", "*");
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
      LocalRegionClient client = new LocalRegionClient(bed.base, clientConfig, new LocalRegionClientMetrics(new NoOpMetricsFactory()), null);
      waitForRouting(bed, client);
      CountDownLatch closures = new CountDownLatch(1);
      client.connect("127.0.0.1:" + bed.port, "127.0.0.1", "origin", "agent", "auth", "space", "key", "{}", ConnectionMode.Full, new SimpleEvents() {
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
          Assert.assertEquals(928828, code);
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
