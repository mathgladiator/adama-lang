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
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.mocks.*;
import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceClientTests {
  @Test
  public void persistent() throws Exception {
    try (TestBed bed =
        new TestBed(
            10001,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
      try (InstanceClient client = bed.makeClient()) {
        {
          AssertCreateFailure failure = new AssertCreateFailure();
          client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", null, "{}", failure);
          Assert.assertFalse(client.ping(1250));
          failure.await(720955);
        }
        bed.startServer();
        Assert.assertTrue(client.ping(7500));
        {
          AssertCreateSuccess success = new AssertCreateSuccess();
          client.create("127.0.0.1", "origin", "nope", "nope", "space", "2", "123", "{}", success);
          success.await();
        }
        bed.stopServer();
        int attempt = 25;
        while (client.ping(100) && attempt-- > 0) {
          System.err.println("Still alive");
        }
        Assert.assertFalse(client.ping(1250));
        {
          bed.startServer();
          Assert.assertTrue(client.ping(2500));
          AssertCreateSuccess success = new AssertCreateSuccess();
          client.create("127.0.0.1", "origin", "nope", "nope", "space", "3", "123", "{}", success);
          success.await();
        }
      }
    }
  }

  @Test
  public void sendAndDisconnect() throws Exception {
    try (TestBed bed =
        new TestBed(
            10003,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 1000; } message Y { int z; } channel foo(Y y) { x += y.z; } view int z; bubble<who, viewer> zpx = viewer.z + x;")) {
      bed.startServer();
      MockEvents events = new MockEvents();
      Runnable happy = events.latchAt(5);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", null, events);
        Remote remote = events.getRemote();
        remote.update("{\"z\":100}");
        remote.send("foo", "marker", "{\"z\":\"100\"}", new Callback<Integer>() {
          @Override
          public void success(Integer value) {
            remote.disconnect();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("error!:" + ex.code);
          }
        });
        happy.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":1000,\"zpx\":1000},\"seq\":4}");
        events.assertWrite(2, "DELTA:{\"data\":{\"zpx\":1100},\"seq\":5}");
        events.assertWrite(3, "DELTA:{\"data\":{\"x\":1100,\"zpx\":1200},\"seq\":6}");
        events.assertWrite(4, "DISCONNECTED");
      }
    }
  }

  @Test
  public void disconnectThenSendFailure() throws Exception {
    try (TestBed bed =
        new TestBed(
            10004,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      AtomicInteger errorCode = new AtomicInteger(1);
      CountDownLatch disconnected = new CountDownLatch(1);
      MockEvents events =
          new MockEvents() {
            Remote remote = null;

            @Override
            public void connected(Remote remote) {
              this.remote = remote;
              super.connected(remote);
            }

            @Override
            public void delta(String data) {
              remote.disconnect();
              super.delta(data);
            }

            @Override
            public void disconnected() {
              remote.send("foo", "marker1", "{\"z\":\"100\"}", new Callback<Integer>() {
                @Override
                public void success(Integer value) {

                }

                @Override
                public void failure(ErrorCodeException ex) {
                  errorCode.set(ex.code);
                  disconnected.countDown();
                }
              });
              super.disconnected();
            }
          };
      Runnable happy = events.latchAt(3);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("127.0.0.1", "origin","nope", "test", "space", "1", "{}", SecureAssetUtil.makeAssetKeyHeader(), events);
        happy.run();
        Assert.assertTrue(disconnected.await(5000, TimeUnit.MILLISECONDS));
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DISCONNECTED");
        Assert.assertEquals(769085, errorCode.get());
      }
    }
  }

  @Test
  public void cantAttachPolicy() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed bed =
        new TestBed(
            10005,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      CountDownLatch cantAttachLatch = new CountDownLatch(1);
      MockEvents events =
          new MockEvents() {
            @Override
            public void connected(Remote remote) {
              remote.canAttach(new Callback<>() {
                @Override
                public void success(Boolean value) {
                  if (!value) {
                    remote.disconnect();
                    cantAttachLatch.countDown();
                  }
                }

                @Override
                public void failure(ErrorCodeException ex) {
                }
              });
              super.connected(remote);
            }
          };
      Runnable happy = events.latchAt(3);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", null, events);
        happy.run();
        Assert.assertTrue(cantAttachLatch.await(2000, TimeUnit.MILLISECONDS));
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DISCONNECTED");
      }
    }
  }

  @Test
  public void canAttachThenAttach() throws Exception {
    try (TestBed bed =
        new TestBed(
            10006,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } @can_attach { return true; } @attached (who, what) { x++; } ")) {
      bed.startServer();
      CountDownLatch canAttachLatch = new CountDownLatch(1);
      MockEvents events =
          new MockEvents() {
            @Override
            public void connected(Remote remote) {
              remote.canAttach(new Callback<Boolean>() {
                @Override
                public void success(Boolean value) {
                  if (value) {
                    remote.attach("id", "name", "text/json", 42, "x", "y", new Callback<Integer>() {
                      @Override
                      public void success(Integer value) {
                        remote.disconnect();
                        canAttachLatch.countDown();
                      }
                      @Override
                      public void failure(ErrorCodeException ex) {
                        System.err.println("didNOT");
                      }
                    });
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {

                }
              });
              super.connected(remote);
            }
          };
      Runnable happy = events.latchAt(4);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("127.0.0.1", "origin","nope", "nope", "space", "1", "{}", null, events);
        happy.run();
        Assert.assertTrue(canAttachLatch.await(2000, TimeUnit.MILLISECONDS));
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":124},\"seq\":6}");
        events.assertWrite(3, "DISCONNECTED");
      }
    }
  }

  @Test
  public void cantAttachDisconnect() throws Exception {
    try (TestBed bed =
        new TestBed(
            10007,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      CountDownLatch cantAttachLatch = new CountDownLatch(1);
      AtomicInteger error = new AtomicInteger(0);
      MockEvents events =
          new MockEvents() {
            Remote remote;

            @Override
            public void connected(Remote remote) {
              this.remote = remote;
              super.connected(remote);
            }

            @Override
            public void delta(String data) {
              this.remote.disconnect();
              super.delta(data);
            }

            @Override
            public void disconnected() {
              remote.canAttach(new Callback<Boolean>() {
                @Override
                public void success(Boolean value) {

                }

                @Override
                public void failure(ErrorCodeException ex) {
                  error.set(ex.code);
                  cantAttachLatch.countDown();
                }
              });
              super.disconnected();
            }
          };
      Runnable happy = events.latchAt(3);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", null, events);
        happy.run();
        Assert.assertTrue(cantAttachLatch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(769085, error.get());
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DISCONNECTED");
      }
    }
  }

  @Test
  public void queueFull() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10007,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      try (InstanceClient client = bed.makeClient()) {
        ArrayList<AssertCreateSuccess> delayed = new ArrayList<>();
        for (int k = 0; k < bed.clientConfig.getClientQueueSize(); k++) {
          AssertCreateSuccess success = new AssertCreateSuccess();
          client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
          delayed.add(success);
        }
        AssertCreateFailure failure = new AssertCreateFailure();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", failure);
        failure.await(737336);

        LatchedVoidCallback scanCallback = new LatchedVoidCallback();
        client.scanDeployments("space", scanCallback);
        scanCallback.assertFail(787514);

        MockEvents connectEvents = new MockEvents();
        Runnable waitError = connectEvents.latchAt(1);
        client.connect("127.0.0.1", "origin", "nope", "nope", "space", "1", "{}", null, connectEvents);
        waitError.run();
        connectEvents.assertWrite(0, "ERROR:702524");

        MockMeteringFlow meteringFlow = new MockMeteringFlow();
        Runnable waitFlowError = meteringFlow.latchAt(1);
        client.startMeteringExchange(meteringFlow);
        waitFlowError.run();
        meteringFlow.assertWrite(0, "ERROR:786495");

        LatchedVoidCallback reflectCallback = new LatchedVoidCallback();
        client.reflect("space", "key", new Callback<String>() {
          @Override
          public void success(String value) {
            reflectCallback.success(null);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            reflectCallback.failure(ex);
          }
        });
        reflectCallback.assertFail(737336);
      }
    }
  }

  @Test
  public void scanDeploymentsSuccessAndFailures() throws Exception {
    try (TestBed bed =
        new TestBed(
            10012,
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      CountDownLatch latch = new CountDownLatch(3);
      try (InstanceClient client = bed.makeClient()) {
        client.scanDeployments("space", new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
            client.scanDeployments("space", new Callback<Void>() {
              @Override
              public void success(Void value) {
                latch.countDown();
                client.scanDeployments("space", new Callback<Void>() {
                  @Override
                  public void success(Void value) {

                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    latch.countDown();
                  }
                });
              }

              @Override
              public void failure(ErrorCodeException ex) {

              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      }
    }
  }

  @Test
  public void heatMonitoring() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10012,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      CountDownLatch latch = new CountDownLatch(3);
      HeatMonitor monitor = (target, cpu, memory) -> latch.countDown();
      try (InstanceClient client = bed.makeClient(monitor)) {
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      }
    }
  }

  @Test
  public void reflect() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10012,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      try (InstanceClient client = bed.makeClient()) {
        CountDownLatch latchGotHappy = new CountDownLatch(1);
        client.reflect("space", "key", new Callback<String>() {
          @Override
          public void success(String value) {
            latchGotHappy.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(latchGotHappy.await(5000, TimeUnit.MILLISECONDS));
      }
    }
  }
}
