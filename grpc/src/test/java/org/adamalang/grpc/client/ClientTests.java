/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.contracts.MeteringStream;
import org.adamalang.grpc.client.contracts.CreateCallback;
import org.adamalang.grpc.client.contracts.SimpleEvents;
import org.adamalang.grpc.client.sm.Connection;
import org.adamalang.grpc.mocks.NaughtyServer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
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
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      Client client = new Client(bed.identity, new ClientMetrics(new NoOpMetricsFactory()), null);
      try {
        client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:12500"));
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
        client.create("me", "dev", "space", "key1", null, "{}", new CreateCallback() {
          @Override
          public void created() {
            latchCreatedKey.countDown();
          }

          @Override
          public void error(int code) {
            System.err.println("CODE:" + code);
          }
        });
        Assert.assertTrue(latchCreatedKey.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch latchGotConnected = new CountDownLatch(1);
        CountDownLatch latchGotData = new CountDownLatch(1);
        CountDownLatch latchGotDisconnect = new CountDownLatch(1);
        Connection connection = client.connect("me", "dev", "space", "key1", "{}", new SimpleEvents() {
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
            Assert.assertEquals(969806, ex.code);
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
  public void no_capacity() throws Exception {
    try (TestBed bed =
             new TestBed(
                 12500,
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      Client client = new Client(bed.identity, new ClientMetrics(new NoOpMetricsFactory()), null);
      try {
        CountDownLatch latch1Failed = new CountDownLatch(1);
        client.create("me", "dev", "space", "key1", null, "{}", new CreateCallback() {
          @Override
          public void created() {
          }

          @Override
          public void error(int code) {
            Assert.assertEquals(912447, code);
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
            Assert.assertEquals(909436, code);
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
            Assert.assertEquals(969806, ex.code);
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
                 12501,
                 "@static { create(who) { return false; } } @connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.25; } #p { x++; } ")) {
      bed.startServer();
      Client client = new Client(bed.identity, new ClientMetrics(new NoOpMetricsFactory()), null);
      try {
        client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:12501"));
        CountDownLatch latchFound = new CountDownLatch(1);
        for (int k = 0; k < 10; k++) {
          client.routing().get(
              "space",
              "key",
              new Consumer<String>() {
                @Override
                public void accept(String s) {
                  if (s != null) {
                    latchFound.countDown();
                  }
                }
              });
          latchFound.await(500, TimeUnit.MILLISECONDS);
        }
        Assert.assertTrue(latchFound.await(1000, TimeUnit.MILLISECONDS));
        CountDownLatch latchFailed = new CountDownLatch(1);
        client.create("me", "dev", "space", "key1", null, "{}", new CreateCallback() {
          @Override
          public void created() {
          }

          @Override
          public void error(int code) {
            Assert.assertEquals(134259, code);
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
  public void bad_client() throws Exception {
    try (NaughtyServer server = NaughtyServer.start().port(12505).establish(true).inventory("space").billing(false).build()) {
      Client client = new Client(server.identity, new ClientMetrics(new NoOpMetricsFactory()), null);
      client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:12505"));
      CountDownLatch latchFound = new CountDownLatch(1);
      for (int k = 0; k < 10; k++) {
        client.routing().get(
            "space",
            "key",
            new Consumer<String>() {
              @Override
              public void accept(String s) {
                if (s != null) {
                  latchFound.countDown();
                }
              }
            });
        latchFound.await(1500, TimeUnit.MILLISECONDS);
      }
      CountDownLatch failure1 = new CountDownLatch(1);
      CountDownLatch failure2 = new CountDownLatch(1);
      client.notifyDeployment("127.0.0.1:12505", "*");
      client.randomMeteringExchange(
          new MeteringStream() {
            @Override
            public void handle(String target, String batch, Runnable after) {
              after.run();
            }

            @Override
            public void failure(int code) {
              Assert.assertEquals(782348, code);
              failure1.countDown();
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
          Assert.assertEquals(969806, ex.code);
          failure2.countDown();
        }
      });
      client.reflect("space", "y", new Callback<String>() {
        @Override
        public void success(String value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(791567, ex.code);
          failure2.countDown();
        }
      });
      Assert.assertTrue(failure1.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(failure2.await(2000, TimeUnit.MILLISECONDS));
    }
  }

  @Test
  public void too_much_queue() throws Exception {
    try (NaughtyServer server = NaughtyServer.start().port(12505).establish(false).inventory("space").billing(false).build()) {
      Client client = new Client(server.identity, new ClientMetrics(new NoOpMetricsFactory()), null);
      client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:12505"));
      CountDownLatch latchFound = new CountDownLatch(1);
      for (int k = 0; k < 10; k++) {
        client.routing().get(
            "space",
            "key",
            new Consumer<String>() {
              @Override
              public void accept(String s) {
                if (s != null) {
                  latchFound.countDown();
                }
              }
            });
        latchFound.await(1500, TimeUnit.MILLISECONDS);
      }
      CountDownLatch failure1 = new CountDownLatch(1);
      CountDownLatch failure2 = new CountDownLatch(1);
      CountDownLatch failure3 = new CountDownLatch(1);
      for (int k = 0; k < 512; k++) {
        client.notifyDeployment("127.0.0.1:12505", "*");
      }
      client.randomMeteringExchange(
          new MeteringStream() {
            @Override
            public void handle(String target, String batch, Runnable after) {
              after.run();
            }

            @Override
            public void failure(int code) {
              Assert.assertEquals(998499, code);
              failure1.countDown();
            }

            @Override
            public void finished() {}
          });
      client.reflect("space", "y", new Callback<String>() {
        @Override
        public void success(String value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(983117, ex.code);
          failure2.countDown();
        }
      });
      client.create("x", "y", "space", "key", null, "{}", new CreateCallback() {
        @Override
        public void created() {

        }

        @Override
        public void error(int code) {
          Assert.assertEquals(996436, code);
          failure3.countDown();
        }
      });
      Assert.assertTrue(failure1.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(failure2.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(failure3.await(2000, TimeUnit.MILLISECONDS));
    }
  }
}
