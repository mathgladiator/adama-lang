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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.mocks.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceClientTests {
  @Test
  public void stubPersistent() throws Exception {
    try (TestBed bed =
        new TestBed(
            10001,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
      try (InstanceClient client = bed.makeClient()) {
        {
          AssertCreateFailure failure = new AssertCreateFailure();
          client.create("origin", "nope", "nope", "space", "1", null, "{}", failure);
          Assert.assertFalse(client.ping(2500));
          failure.await(720955);
        }
        bed.startServer();
        Assert.assertTrue(client.ping(15000));
        {
          AssertCreateSuccess success = new AssertCreateSuccess();
          client.create("origin", "nope", "nope", "space", "2", "123", "{}", success);
          success.await();
        }
        // TODO: meaningful way of shutting down the server
      }
    }
  }

  @Test
  public void sendAndDisconnect() throws Exception {
    try (TestBed bed =
        new TestBed(
            10003,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; } view int z; bubble<who, viewer> zpx = viewer.z + x;")) {
      bed.startServer();
      MockEvents events =
          new MockEvents() {
            @Override
            public void connected(Remote remote) {
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
              super.connected(remote);
            }
          };
      Runnable happy = events.latchAt(5);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("origin", "nope", "test", "space", "1", "{}", events);
        happy.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123,\"zpx\":123},\"seq\":4}");
        events.assertWrite(2, "DELTA:{\"data\":{\"zpx\":223},\"seq\":5}");
        events.assertWrite(3, "DELTA:{\"data\":{\"x\":223,\"zpx\":323},\"seq\":7}");
        events.assertWrite(4, "DISCONNECTED");
      }
    }
  }

  @Test
  public void disconnectThenSendFailure() throws Exception {
    try (TestBed bed =
        new TestBed(
            10004,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
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
        client.create("origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("origin","nope", "test", "space", "1", "{}", events);
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
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
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
        client.create("origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("origin", "nope", "test", "space", "1", "{}", events);
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
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } @can_attach(who) { return true; } @attached (who, what) { x++; } ")) {
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
        client.create("origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("origin","nope", "nope", "space", "1", "{}", events);
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
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
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
        client.create("origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("origin", "nope", "test", "space", "1", "{}", events);
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
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      try (InstanceClient client = bed.makeClient()) {
        ArrayList<AssertCreateSuccess> delayed = new ArrayList<>();
        for (int k = 0; k < InstanceClient.WAITING_QUEUE; k++) {
          AssertCreateSuccess success = new AssertCreateSuccess();
          client.create("origin", "nope", "nope", "space", "1", "123", "{}", success);
          delayed.add(success);
        }
        AssertCreateFailure failure = new AssertCreateFailure();
        client.create("origin", "nope", "nope", "space", "1", "123", "{}", failure);
        failure.await(737336);

        LatchedVoidCallback scanCallback = new LatchedVoidCallback();
        client.scanDeployments("space", scanCallback);
        scanCallback.assertFail(787514);

        MockEvents connectEvents = new MockEvents();
        Runnable waitError = connectEvents.latchAt(1);
        client.connect("origin", "nope", "nope", "space", "1", "{}", connectEvents);
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

  /*
  @Test
  public void cantAttachSocketDisconnect() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed bed =
        new TestBed(
            10008,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      MockClentLifecycle lifecycle = new MockClentLifecycle();
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
              try {
                bed.stopServer();
              } catch (Exception ex) {

              }
              super.delta(data);
            }

            @Override
            public void disconnected() {
              remote.canAttach(
                  new AskAttachmentCallback() {
                    @Override
                    public void allow() {}

                    @Override
                    public void reject() {}

                    @Override
                    public void error(int code) {
                      error.set(code);
                      cantAttachLatch.countDown();
                    }
                  });
              super.disconnected();
            }
          };
      Runnable happy = events.latchAt(3);
      try (InstanceClient client =
          new InstanceClient(
              bed.identity,
              metrics,
              null,
              "127.0.0.1:10008",
              bed.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  AssertCreateSuccess success = new AssertCreateSuccess();
                  client.create("nope", "nope", "space", "1", "123", "{}", success);
                  success.await();
                  client.connect("nope", "test", "space", "1", "{}", events);
                  lifecycle.connected(client);
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {
                  lifecycle.disconnected(client);
                }
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        bed.startServer();
        happy.run();
        Assert.assertTrue(cantAttachLatch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(701452, error.get());
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DISCONNECTED");
      }
    }
  }

  @Test
  public void attachSocketDisconnect() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed bed =
        new TestBed(
            10009,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      MockClentLifecycle lifecycle = new MockClentLifecycle();
      CountDownLatch attachLatch = new CountDownLatch(1);
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
              try {
                bed.stopServer();
              } catch (Exception ex) {

              }
              super.delta(data);
            }

            @Override
            public void disconnected() {
              remote.attach(
                  "id",
                  "name",
                  "text/json",
                  42,
                  "x",
                  "y",
                  new SeqCallback() {
                    @Override
                    public void success(int seq) {}

                    @Override
                    public void error(int code) {
                      error.set(code);
                      attachLatch.countDown();
                    }
                  });
              super.disconnected();
            }
          };
      Runnable happy = events.latchAt(3);
      try (InstanceClient client =
          new InstanceClient(
              bed.identity,
              metrics,
              null,
              "127.0.0.1:10009",
              bed.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  AssertCreateSuccess success = new AssertCreateSuccess();
                  client.create("nope", "nope", "space", "1", "123", "{}", success);
                  success.await();
                  client.connect("nope", "test", "space", "1", "{}", events);
                  lifecycle.connected(client);
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {
                  lifecycle.disconnected(client);
                }
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        bed.startServer();
        happy.run();
        Assert.assertTrue(attachLatch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(786442, error.get());
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DISCONNECTED");
      }
    }
  }

  @Test
  public void socketDisconnectThenSendFailure() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed bed =
        new TestBed(
            10010,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      MockClentLifecycle lifecycle = new MockClentLifecycle();
      AtomicInteger errorCodeSeq = new AtomicInteger(0);
      CountDownLatch latch = new CountDownLatch(1);
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
              try {
                bed.stopServer();
              } catch (Exception ex) {

              }
              super.delta(data);
            }

            @Override
            public void disconnected() {
              remote.send(
                  "foo",
                  "marker1",
                  "{\"z\":\"100\"}",
                  new SeqCallback() {
                    @Override
                    public void success(int seq) {}

                    @Override
                    public void error(int code) {
                      errorCodeSeq.set(code);
                      latch.countDown();
                    }
                  });
              super.disconnected();
            }
          };
      Runnable happy = events.latchAt(3);
      try (InstanceClient client =
          new InstanceClient(
              bed.identity,
              metrics,
              null,
              "127.0.0.1:10010",
              bed.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  AssertCreateSuccess success = new AssertCreateSuccess();
                  client.create("nope", "nope", "space", "1", "123", "{}", success);
                  success.await();
                  client.connect("nope", "test", "space", "1", "{}", events);
                  lifecycle.connected(client);
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {
                  lifecycle.disconnected(client);
                }
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        bed.startServer();
        happy.run();
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DISCONNECTED");
        Assert.assertEquals(777231, errorCodeSeq.get());
      }
    }
  }

  @Test
  public void noSocket() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed bed =
        new TestBed(
            10011,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      MockClentLifecycle lifecycle = new MockClentLifecycle();
      MockEvents events =
          new MockEvents() {
            Remote remote = null;

            @Override
            public void connected(Remote remote) {
              this.remote = remote;
              super.connected(remote);
            }
          };
      Runnable happy = events.latchAt(1);
      try (InstanceClient client =
          new InstanceClient(
              bed.identity,
              metrics,
              null,
              "127.0.0.1:10011",
              bed.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  client.close();
                  client.connect("nope", "test", "space", "1", "{}", events);
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {
                  lifecycle.disconnected(client);
                }
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        bed.startServer();
        happy.run();
        events.assertWrite(0, "DISCONNECTED");
      }
    }
  }
*/
  @Test
  public void scanDeploymentsSuccessAndFailures() throws Exception {
    try (TestBed bed =
        new TestBed(
            10012,
            "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
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
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
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
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
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

  /*
  @Test
  public void naughtyFailureReflection() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (NaughtyServer server = NaughtyServer.start().port(11100).establish(true).build()) {
      CountDownLatch avail = new CountDownLatch(1);
      try (InstanceClient client =
          new InstanceClient(
              server.identity,
              metrics,
              null,
              "127.0.0.1:11100",
              server.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  avail.countDown();
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {}
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        Assert.assertTrue(avail.await(2000, TimeUnit.MILLISECONDS));
        CountDownLatch failed = new CountDownLatch(1);
        client.reflect(
            "space",
            "key",
            new Callback<String>() {
              @Override
              public void success(String value) {}

              @Override
              public void failure(ErrorCodeException ex) {
                Assert.assertEquals(791567, ex.code);
                failed.countDown();
              }
            });
        Assert.assertTrue(failed.await(2000, TimeUnit.MILLISECONDS));
      }
    }
  }

  @Test
  public void naughtyFailureConnectNotReady() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (NaughtyServer server = NaughtyServer.start().port(11101).establish(false).build()) {
      try (InstanceClient client =
          new InstanceClient(
              server.identity,
              metrics,
              null,
              "127.0.0.1:11101",
              server.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {}

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {}
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        CountDownLatch disconnect = new CountDownLatch(1);
        client.connect(
            "a",
            "a",
            "space",
            "key", "{}",
            new Events() {
              @Override
              public void connected(Remote remote) {}

              @Override
              public void delta(String data) {}

              @Override
              public void error(int code) {}

              @Override
              public void disconnected() {
                disconnect.countDown();
              }
            });
        Assert.assertTrue(disconnect.await(5000, TimeUnit.MILLISECONDS));
      }
    }
  }

  @Test
  public void sampleBillingExchangeHappy() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (NaughtyServer server =
        NaughtyServer.start().port(11102).establish(true).billing(true).build()) {
      CountDownLatch avail = new CountDownLatch(1);
      try (InstanceClient client =
          new InstanceClient(
              server.identity,
              metrics,
              null,
              "127.0.0.1:11102",
              server.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  avail.countDown();
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {}
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        Assert.assertTrue(avail.await(2000, TimeUnit.MILLISECONDS));
        CountDownLatch finished = new CountDownLatch(1);
        ArrayList<String> handled = new ArrayList<>();
        client.startMeteringExchange(
            new MeteringStream() {
              @Override
              public void handle(String target, String batch, Runnable after) {
                handled.add(target + ":" + batch);
                System.err.println("SEE:" + target + ":" + batch);
                after.run();
              }

              @Override
              public void failure(int code) {}

              @Override
              public void finished() {
                finished.countDown();
              }
            });
        Assert.assertTrue(finished.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(5, handled.size());
        Assert.assertEquals("127.0.0.1:11102:batch:4", handled.get(0));
        Assert.assertEquals("127.0.0.1:11102:batch:3", handled.get(1));
        Assert.assertEquals("127.0.0.1:11102:batch:2", handled.get(2));
        Assert.assertEquals("127.0.0.1:11102:batch:1", handled.get(3));
        Assert.assertEquals("127.0.0.1:11102:batch:0", handled.get(4));
      }
    }
  }

  @Test
  public void sampleBillingExchangeSad() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (NaughtyServer server =
        NaughtyServer.start().port(11103).establish(true).billing(false).build()) {
      CountDownLatch avail = new CountDownLatch(1);
      try (InstanceClient client =
          new InstanceClient(
              server.identity,
              metrics,
              null,
              "127.0.0.1:11103",
              server.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  avail.countDown();
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {}
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        Assert.assertTrue(avail.await(2000, TimeUnit.MILLISECONDS));
        CountDownLatch finished = new CountDownLatch(1);
        client.startMeteringExchange(
            new MeteringStream() {
              @Override
              public void handle(String target, String batch, Runnable after) {
                after.run();
              }

              @Override
              public void failure(int code) {
                Assert.assertEquals(782348, code);
                finished.countDown();
              }

              @Override
              public void finished() {}
            });
        Assert.assertTrue(finished.await(2000, TimeUnit.MILLISECONDS));
      }
    }
  }

  @Test
  public void lateClientBinding() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (NaughtyServer server = NaughtyServer.start().port(11104).establish(false).build()) {
      CountDownLatch avail = new CountDownLatch(1);
      try (InstanceClient client =
          new InstanceClient(
              server.identity,
              metrics,
              null,
              "127.0.0.1:11104",
              server.clientExecutor,
              new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                  avail.countDown();
                }

                @Override
                public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                @Override
                public void disconnected(InstanceClient client) {}
              },
              (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
              })) {
        client.close();
        server
            .getResponder()
            .onNext(
                StreamMessageServer.newBuilder()
                    .setEstablish(Establish.newBuilder().build())
                    .build());
      }
    }
  }
  */
}
