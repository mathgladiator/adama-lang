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
import org.adamalang.common.rate.TokenGrant;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.contracts.ReadOnlyRemote;
import org.adamalang.runtime.sys.AuthResponse;
import org.adamalang.runtime.sys.ConnectionMode;
import org.adamalang.runtime.sys.capacity.CurrentLoad;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.client.mocks.SimpleIntCallback;
import org.adamalang.net.mocks.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
                 "@static { create { return true; } send { return @who.isOverlord(); } } @connected { return true; } public int x; @construct { x = 1000; } message Y { int z; } channel foo(Y y) { x += y.z; } view int z; bubble zpx = @viewer.z + x;")) {
      bed.startServer();
      MockEvents events = new MockEvents();
      Runnable happy = events.latchAt(7);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.connect("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", ConnectionMode.Full, events);
        Remote remote = events.getRemote();
        LatchedVoidCallback updateRan = new LatchedVoidCallback();
        remote.update("{\"z\":100}", updateRan);
        updateRan.assertSuccess();
        SimpleIntCallback sic = new SimpleIntCallback();
        client.directSend("127.0.0.1", "origin", "overlord", "overlord", "space", "1", null, "foo", "{\"z\":1000}", sic);
        sic.assertSuccess(5);
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
        events.assertWrite(1, "DELTA:{\"view-state-filter\":[\"z\"]}");
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":1000,\"zpx\":1000},\"seq\":4}");
        events.assertWrite(3, "DELTA:{\"data\":{\"zpx\":1100}}");
        events.assertWrite(4, "DELTA:{\"data\":{\"x\":2000,\"zpx\":2100},\"seq\":5}");
        events.assertWrite(5, "DELTA:{\"data\":{\"x\":2100,\"zpx\":2200},\"seq\":6}");
        events.assertWrite(6, "DISCONNECTED");
      }
    }
  }


  @Test
  public void observe() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10003,
                 "@static { create { return true; } send { return @who.isOverlord(); } } @connected { return true; } public int x; @construct { x = 1000; } message Y { int z; } channel foo(Y y) { x += y.z; } view int z; bubble zpx = @viewer.z + x;")) {
      bed.startServer();
      MockReadOnlyEvents events = new MockReadOnlyEvents();
      Runnable gotFirst = events.latchAt(3);
      Runnable gotSecond = events.latchAt(4);
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        client.observe("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", events);
        gotFirst.run();
        ReadOnlyRemote remote = events.getRemote();
        LatchedVoidCallback updateRan = new LatchedVoidCallback();
        remote.update("{\"z\":100}", updateRan);
        updateRan.assertSuccess();
        gotSecond.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"view-state-filter\":[\"z\"]}");
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":421369,\"zpx\":421369},\"seq\":0}");
        events.assertWrite(3, "DELTA:{\"data\":{\"zpx\":421469},\"seq\":0}");
      }
    }
  }


  @Test
  public void forceBackup() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10003,
                 "@static { create { return true; } send { return @who.isOverlord(); } } @connected { return true; } public int x; @construct { x = 1000; } message Y { int z; } channel foo(Y y) { x += y.z; } view int z; bubble zpx = @viewer.z + x;")) {
      bed.startServer();
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        {
          LatchedStringCallback callback = new LatchedStringCallback();
          client.forceBackup("127.0.0.1", "origin", "nope", "nope", "space", "1", callback);
          callback.assertSuccess("backup-via-net");
        }
        {
          LatchedStringCallback callback = new LatchedStringCallback();
          client.forceBackup("127.0.0.1", "origin", "nope", "nope", "space", "2", callback);
          callback.assertFail("625676");
        }
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
        client.connect("127.0.0.1", "origin","nope", "test", "space", "1", "{}", ConnectionMode.Full, events);
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
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(new NoOpMetricsFactory());
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
        client.connect("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", ConnectionMode.Full, events);
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
            "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } @can_attach { return true; } @attached (what) { x++; } ")) {
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
        client.connect("127.0.0.1", "origin","nope", "nope", "space", "1", "{}", ConnectionMode.Full, events);
        happy.run();
        Assert.assertTrue(canAttachLatch.await(2000, TimeUnit.MILLISECONDS));
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":124},\"seq\":5}");
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
        client.connect("127.0.0.1", "origin", "nope", "test", "space", "1", "{}", ConnectionMode.Full, events);
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
        client.connect("127.0.0.1", "origin", "nope", "nope", "space", "1", "{}", ConnectionMode.Full, connectEvents);
        waitError.run();
        connectEvents.assertWrite(0, "ERROR:702524");

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
        reflectCallback.assertFail(787440);
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

  @Test
  public void rate() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10019,
                 "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      bed.startServer();
      try (InstanceClient client = bed.makeClient()) {
        CountDownLatch latchGotHappy = new CountDownLatch(1);
        client.rateLimit("0.0.0.0", "session", "resource", "auth", new Callback<TokenGrant>() {
          @Override
          public void success(TokenGrant value) {
            Assert.assertEquals(5, value.tokens);
            Assert.assertEquals(250, value.millseconds);
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

  @Test
  public void auth() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10013,
                 "@static { create { return true; } invent { return true; } } @connected { return true; } @authorize (ur, pw) { if (pw == \"pass\") { return ur; } abort; }")) {
      bed.startServer();
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        {
          CountDownLatch latchGotHappy = new CountDownLatch(1);
          client.authorize("127.0.0.1", "origin", "space", "1", "user", "pass", "yop", new Callback<String>() {
            @Override
            public void success(String value) {
              System.err.println(value);
              latchGotHappy.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              ex.printStackTrace();
            }
          });
          Assert.assertTrue(latchGotHappy.await(5000, TimeUnit.MILLISECONDS));
        }
        {
          CountDownLatch latchGotFailure = new CountDownLatch(1);
          client.authorize("127.0.0.1", "origin", "space", "1", "user", "yop", null, new Callback<String>() {
            @Override
            public void success(String value) {

            }

            @Override
            public void failure(ErrorCodeException ex) {
              latchGotFailure.countDown();
            }
          });
          Assert.assertTrue(latchGotFailure.await(5000, TimeUnit.MILLISECONDS));
        }


      }
    }
  }

  @Test
  public void authpipe() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10013,
                 "@static { create { return true; } invent { return true; } } @connected { return true; } message M { bool fail; } @authorization (M m) { if (m.fail) abort; return {agent:\"1\", hash:\"h\"}; }")) {
      bed.startServer();
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        {
          CountDownLatch latchGotHappy = new CountDownLatch(1);
          client.authorization("127.0.0.1", "origin", "space", "1", "{}", new Callback<AuthResponse>() {
            @Override
            public void success(AuthResponse value) {
              System.err.println(value.agent + ";" + value.hash);
              latchGotHappy.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              ex.printStackTrace();
            }
          });
          Assert.assertTrue(latchGotHappy.await(5000, TimeUnit.MILLISECONDS));
        }
        {
          CountDownLatch latchGotFailure = new CountDownLatch(1);
          client.authorization("127.0.0.1", "origin", "space", "1", "{\"fail\":true}", new Callback<AuthResponse>() {
            @Override
            public void success(AuthResponse value) {

            }

            @Override
            public void failure(ErrorCodeException ex) {
              latchGotFailure.countDown();
            }
          });
          Assert.assertTrue(latchGotFailure.await(5000, TimeUnit.MILLISECONDS));
        }
      }
    }
  }


  @Test
  public void drain_and_query_load() throws Exception {
    try (TestBed bed =
             new TestBed(
                 10014,
                 "@static { create { return true; } invent { return true; } } @connected { return true; } message M { bool fail; } @authorization (M m) { if (m.fail) abort; return {agent:\"1\", hash:\"h\"}; }")) {
      bed.startServer();
      try (InstanceClient client = bed.makeClient()) {
        AssertCreateSuccess success = new AssertCreateSuccess();
        client.create("127.0.0.1", "origin", "nope", "nope", "space", "1", "123", "{}", success);
        success.await();
        {
          AtomicBoolean loadSuccess = new AtomicBoolean(false);
          CountDownLatch gotLoad = new CountDownLatch(1);
          client.getCurrentLoad(new Callback<CurrentLoad>() {
            @Override
            public void success(CurrentLoad inventory) {
              System.err.println("pre-drain-load:" + inventory.documents + ", " + inventory.connections);
              loadSuccess.set(true);
              gotLoad.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              gotLoad.countDown();
            }
          });
          Assert.assertTrue(gotLoad.await(5000, TimeUnit.MILLISECONDS));
          Assert.assertTrue(loadSuccess.get());
        }
        {
          AtomicBoolean drainSuccess = new AtomicBoolean(false);
          CountDownLatch didDrain = new CountDownLatch(1);
          client.drain(new Callback<Void>() {
            @Override
            public void success(Void value) {
              drainSuccess.set(true);
              didDrain.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              didDrain.countDown();
            }
          });
          Assert.assertTrue(didDrain.await(5000, TimeUnit.MILLISECONDS));
          Assert.assertTrue(drainSuccess.get());
        }
        {
          AtomicBoolean loadSuccess = new AtomicBoolean(false);
          CountDownLatch gotLoad = new CountDownLatch(1);
          client.getCurrentLoad(new Callback<CurrentLoad>() {
            @Override
            public void success(CurrentLoad inventory) {
              System.err.println("post-drain-load:" + inventory.documents + ", " + inventory.connections);
              loadSuccess.set(true);
              gotLoad.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              gotLoad.countDown();
            }
          });
          Assert.assertTrue(gotLoad.await(5000, TimeUnit.MILLISECONDS));
          Assert.assertTrue(loadSuccess.get());
        }
      }
    }
  }
}
