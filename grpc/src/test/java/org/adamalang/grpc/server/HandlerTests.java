/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.server;

import io.grpc.stub.StreamObserver;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.proto.*;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HandlerTests {
  private static final String BAD_CODE = "@can_attach(who) { int x = 1; while(true) { x++; } return true; } @attached(who, what) { while(true) {} } @static { create(who) { return true; } } @connected(who) { return true; } message M {} channel foo(M y) { while(true) {} }  ";
  private static final String OK_CODE = "@can_attach(who) { return true; } @attached(who, what) { } @static { create(who) { return true; } } @connected(who) { return true; } message M {} channel foo(M y) { }";

  @Test
  public void entropy() {
    Assert.assertEquals("120", Handler.fixEntropy("x"));
    Assert.assertNull(Handler.fixEntropy(""));
  }

  public static class HandlerBed implements AutoCloseable, TimeSource {
    private final SimpleExecutor executor;
    public final MachineIdentity identity;
    public final ServerNexus nexus;
    public final CoreService service;
    public final Handler handler;
    private final ArrayDeque<StreamMessageServer> downstream;
    public final StreamObserver<StreamMessageClient> upstream;
    public boolean skipData = false;
    public boolean dropPatches = false;
    private ArrayDeque<Runnable> delayedOps = new ArrayDeque<>();
    public boolean delayDataServiceUpdates = false;
    public final DiskMeteringBatchMaker maker;

    public long time;

    @Override
    public long nowMilliseconds() {
      return time;
    }


    public Runnable release() throws Exception {
      for (int k = 0; k < 50; k++) {
        synchronized (delayedOps) {
          while (delayedOps.size() > 0) {
            return delayedOps.removeFirst();
          }
        }
        Thread.sleep(100);
      }
      Assert.fail();
      return null;
    }

    public StreamMessageServer pull() throws Exception {
      for (int k = 0; k < 50; k++) {
        synchronized (downstream) {
          if (downstream.size() > 0) {
            StreamMessageServer pulled = downstream.removeFirst();
            if (pulled.getByTypeCase() == StreamMessageServer.ByTypeCase.HEARTBEAT) {
              continue;
            }
            if (pulled.getByTypeCase() == StreamMessageServer.ByTypeCase.DATA && skipData) {
              continue;
            }
            return pulled;
          }
        }
        Thread.sleep(100);
      }
      Assert.fail();
      return null;
    }

    public StreamMessageServer poll() throws Exception {
      synchronized (downstream) {
        while (downstream.size() > 0) {
          StreamMessageServer pulled = downstream.removeFirst();
          if (pulled.getByTypeCase() == StreamMessageServer.ByTypeCase.HEARTBEAT) {
            continue;
          }
          if (pulled.getByTypeCase() == StreamMessageServer.ByTypeCase.DATA && skipData) {
            continue;
          }
          return pulled;
        }
      }
      return null;
    }

    public final CountDownLatch multiplexCompleted = new CountDownLatch(1);

    public HandlerBed(String code) throws Exception {
      executor = SimpleExecutor.create("exec");
      JsonStreamWriter planWriter = new JsonStreamWriter();
      planWriter.beginObject();
      planWriter.writeObjectFieldIntro("versions");
      planWriter.beginObject();
      planWriter.writeObjectFieldIntro("x");
      planWriter.writeString(code);
      planWriter.endObject();
      planWriter.writeObjectFieldIntro("default");
      planWriter.writeString("x");
      planWriter.endObject();
      DeploymentPlan plan = new DeploymentPlan(planWriter.toString(), (t, errorCode) -> {});
      DeploymentFactoryBase base = new DeploymentFactoryBase();
      base.deploy("space", plan);
      this.time = 10000;
      this.identity = MachineIdentity.fromFile(TestBed.prefixForLocalhost());
      MeteringPubSub pubSub = new MeteringPubSub(this, base);
      File temp = File.createTempFile("x23", "x23").getParentFile();
      File billing = new File(temp, "billing_" + code.hashCode());
      billing.mkdirs();
      billing.deleteOnExit();
      this.maker = new DiskMeteringBatchMaker(this, executor, billing,15000L);
      ServerMetrics metrics = new ServerMetrics(new NoOpMetricsFactory());
      InMemoryDataService real = new InMemoryDataService(Executors.newSingleThreadExecutor(), this);
      DataService faux =
          new DataService() {
            @Override
            public void get(Key key, Callback<LocalDocumentChange> callback) {
              if (delayDataServiceUpdates) {
                System.err.println("DELAY GET");
                synchronized (delayedOps) {
                  delayedOps.add(() -> real.get(key, callback));
                }
                return;
              }
              real.get(key, callback);
            }

            @Override
            public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
              if (delayDataServiceUpdates) {
                System.err.println("DELAY INIT");
                synchronized (delayedOps) {
                  delayedOps.add(() -> real.initialize(key, patch, callback));
                }
                return;
              }
              real.initialize(key, patch, callback);
            }

            @Override
            public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
              if (delayDataServiceUpdates) {
                System.err.println("DELAY PATCH");
                synchronized (delayedOps) {
                  delayedOps.add(() -> real.patch(key, patches, callback));
                }
                return;
              }

              if (dropPatches) {
                return;
              }
              real.patch(key, patches, callback);
            }

            @Override
            public void compute(
                Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
              real.compute(key, method, seq, callback);
            }

            @Override
            public void compact(Key key, int history, Callback<Integer> callback) {
              real.compact(key, history, callback);
            }

            @Override
            public void delete(Key key, Callback<Void> callback) {
              real.delete(key, callback);
            }
          };
      this.service =
          new CoreService(
              new CoreMetrics(new NoOpMetricsFactory()),
              base, //
              pubSub.publisher(), //
              faux, //
              this,
              1);
      this.nexus = new ServerNexus(identity, service, metrics, base, (x) -> {}, pubSub, maker, -1, 1);
      this.handler = new Handler(nexus);
      this.downstream = new ArrayDeque<>();
      this.upstream = handler.multiplexedProtocol(new StreamObserver<StreamMessageServer>() {
        @Override
        public void onNext(StreamMessageServer streamMessageServer) {
          synchronized (downstream) {
            downstream.add(streamMessageServer);
          }
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onCompleted() {
          multiplexCompleted.countDown();
        }
      });
    }

    private long id = 0;
    public long connect() {
      long _id = id++;
      upstream.onNext(StreamMessageClient.newBuilder().setId(_id).setConnect(StreamConnect.newBuilder().setSpace("space").setKey("key").setAuthority("dev").setAgent("agent").build()).build());
      return _id;
    }

    public void can_attach(long connection) {
      long _id = id++;
      upstream.onNext(StreamMessageClient.newBuilder().setId(_id).setAct(connection).setAsk(StreamAskAttachmentRequest.newBuilder().build()).build());
    }

    public void attach(long connection) {
      long _id = id++;
      upstream.onNext(StreamMessageClient.newBuilder().setId(_id).setAct(connection).setAttach(StreamAttach.newBuilder().setId("id").build()).build());
    }

    public void send(long connection) {
      long _id = id++;
      upstream.onNext(StreamMessageClient.newBuilder().setId(_id).setAct(connection).setSend(StreamSend.newBuilder().setChannel("foo").setMessage("{}").build()).build());
    }

    @Override
    public void close() throws Exception {
      executor.shutdown();
    }

    public void assert_pull_establish() throws Exception {
      Assert.assertEquals(StreamMessageServer.ByTypeCase.ESTABLISH, pull().getByTypeCase());
    }

    public void assert_pull_status_connected() throws Exception {
      StreamMessageServer msg = pull();
      Assert.assertEquals(StreamMessageServer.ByTypeCase.STATUS, msg.getByTypeCase());
      Assert.assertEquals(StreamStatusCode.Connected, msg.getStatus().getCode());
    }

    public void assert_pull_error(int error) throws Exception {
      StreamMessageServer msg = pull();
      Assert.assertEquals(StreamMessageServer.ByTypeCase.ERROR, msg.getByTypeCase());
      Assert.assertEquals(error, msg.getError().getCode());
    }

    public void create() {
      service.create(NtClient.NO_ONE, new Key("space", "key"), "{}", null, new Callback<Void>() {
        @Override
        public void success(Void value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
    }
  }

  @Test
  public void slow_service_handler_disconnected() throws Exception {
    try (HandlerBed bed = new HandlerBed(OK_CODE)) {
      bed.skipData = true;
      bed.delayDataServiceUpdates = true;
      bed.create();
      bed.assert_pull_establish();
      bed.release().run(); // init
      bed.release().run(); // patch connect
      bed.connect();
      bed.upstream.onCompleted();
      Assert.assertTrue(bed.multiplexCompleted.await(5000, TimeUnit.MILLISECONDS));
      bed.release().run(); // get
      bed.release().run(); // patch connect
      bed.release().run(); // patch disconnect
    }
  }

  @Test
  public void disconnect_before_connect_lands() throws Exception {
    try (HandlerBed bed = new HandlerBed(OK_CODE)) {
      bed.skipData = true;
      bed.delayDataServiceUpdates = true;
      bed.create();
      bed.assert_pull_establish();
      bed.release().run(); // init
      bed.release().run(); // patch connect
      long connection = bed.connect();
      bed.upstream.onNext(StreamMessageClient.newBuilder().setId(1000).setAct(connection).setDisconnect(StreamDisconnect.newBuilder().build()).build());
      bed.release().run(); // get
      bed.release().run(); // patch connect
      bed.release().run(); // patch disconnect
    }
  }

  @Test
  public void handler_service_cant_attach_failure_fault() throws Exception {
    try (HandlerBed bed = new HandlerBed(BAD_CODE)) {
      bed.skipData = true;
      bed.create();
      bed.assert_pull_establish();
      long id = bed.connect();
      bed.assert_pull_status_connected();
      bed.can_attach(id);
      bed.assert_pull_error(146569);
    }
  }

  @Test
  public void handler_queue_full() throws Exception {
    try (HandlerBed bed = new HandlerBed(BAD_CODE)) {
      bed.skipData = true;
      bed.create();
      bed.assert_pull_establish();
      long id = bed.connect();
      bed.dropPatches = true;
      for (int k = 0; k < 1000; k++) {
        bed.send(id);
        bed.can_attach(id);
        bed.attach(id);
      }
      HashSet<Integer> codesSeen = new HashSet<>();
      while (codesSeen.size() < 3) {
        StreamMessageServer msg = bed.poll();
        if (msg != null) {
          if (msg.getByTypeCase() == StreamMessageServer.ByTypeCase.ERROR) {
            codesSeen.add(msg.getError().getCode());
            System.err.println(msg.getError().getCode());
          }
        } else {
          Thread.sleep(50);
        }
      }
      Assert.assertTrue(codesSeen.contains(733185));
      Assert.assertTrue(codesSeen.contains(754688));
      Assert.assertTrue(codesSeen.contains(782339));
    }
  }


  @Test
  public void handler_service_attach_failure_fault() throws Exception {
    try (HandlerBed bed = new HandlerBed(BAD_CODE)) {
      bed.skipData = true;
      bed.create();
      bed.assert_pull_establish();
      long id = bed.connect();
      bed.assert_pull_status_connected();
      bed.attach(id);
      bed.assert_pull_error(950384);
    }
  }

  @Test
  public void handler_service_send_failure_fault() throws Exception {
    try (HandlerBed bed = new HandlerBed(BAD_CODE)) {
      bed.skipData = true;
      bed.create();
      bed.assert_pull_establish();
      long id = bed.connect();
      bed.assert_pull_status_connected();
      bed.send(id);
      bed.assert_pull_error(950384);
    }
  }

  @Test
  public void handler_billing_exchange() throws Exception {
    try (HandlerBed bed = new HandlerBed(OK_CODE)) {
      AtomicReference<StreamObserver<BillingForward>> ref = new AtomicReference<>();
      CountDownLatch complete = new CountDownLatch(1);
      CountDownLatch didSomething = new CountDownLatch(1);
      do {
        System.err.println("makign records");
        for (int k = 0; k < 1000 && bed.maker.getNextAvailableBatchId() == null; k++) {
          bed.maker.write(
              new MeterReading(
                  123, 12, "sapce", "hash", new PredictiveInventory.MeteringSample(1, 2, 3, 4, 5)));
          bed.time += 5000;
        }
        CountDownLatch drain = new CountDownLatch(1);
        bed.executor.execute(new NamedRunnable("name") {
          @Override
          public void execute() throws Exception {
            drain.countDown();
          }
        });
        Assert.assertTrue(drain.await(5000, TimeUnit.MILLISECONDS));
        ref.set(bed.handler.billingExchange(new StreamObserver<BillingReverse>() {
          @Override
          public void onNext(BillingReverse billingReverse) {
            switch (billingReverse.getOperationCase()) {
              case FOUND:
                didSomething.countDown();
                ref.get().onNext(BillingForward.newBuilder().setRemove(BillingDeleteBill.newBuilder().setId(billingReverse.getFound().getId()).build()).build());
                return;
              case REMOVED:
                ref.get().onNext(BillingForward.newBuilder().setBegin(BillingBegin.newBuilder().build()).build());
                return;
            }
          }

          @Override
          public void onError(Throwable throwable) {

          }

          @Override
          public void onCompleted() {
            complete.countDown();
          }
        }));
        ref.get().onNext(BillingForward.newBuilder().setBegin(BillingBegin.newBuilder().build()).build());
        Assert.assertTrue(complete.await(5000, TimeUnit.MILLISECONDS));
      } while (!didSomething.await(100, TimeUnit.MILLISECONDS));
    }
  }

  @Test
  public void handler_billing_error() throws Exception {
    try (HandlerBed bed = new HandlerBed(OK_CODE)) {
      AtomicReference<StreamObserver<BillingForward>> ref = new AtomicReference<>();
      CountDownLatch complete = new CountDownLatch(1);
      ref.set(bed.handler.billingExchange(new StreamObserver<BillingReverse>() {
        @Override
        public void onNext(BillingReverse billingReverse) {
          switch (billingReverse.getOperationCase()) {
            case FOUND:
              ref.get().onNext(BillingForward.newBuilder().setRemove(BillingDeleteBill.newBuilder().setId(billingReverse.getFound().getId()).build()).build());
              return;
            case REMOVED:
              ref.get().onNext(BillingForward.newBuilder().setBegin(BillingBegin.newBuilder().build()).build());
              return;
          }
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onCompleted() {
          complete.countDown();
        }
      }));
      ref.get().onError(new NullPointerException());
      Assert.assertTrue(complete.await(5000, TimeUnit.MILLISECONDS));
    }
  }

  @Test
  public void reflection() throws Exception {
    try (HandlerBed bed = new HandlerBed(OK_CODE)) {
      CountDownLatch latchHappy = new CountDownLatch(1);
      CountDownLatch latchSad = new CountDownLatch(1);
      bed.handler.reflect(ReflectRequest.newBuilder().setKey("x").setSpace("space").build(), new StreamObserver<ReflectResponse>() {
        @Override
        public void onNext(ReflectResponse reflectResponse) {
          System.err.println(reflectResponse);
          latchHappy.countDown();
        }

        @Override
        public void onError(Throwable throwable) {
          throwable.printStackTrace();
        }

        @Override
        public void onCompleted() {
        }
      });
      bed.handler.reflect(ReflectRequest.newBuilder().setKey("x").setSpace("sad").build(), new StreamObserver<ReflectResponse>() {
        @Override
        public void onNext(ReflectResponse reflectResponse) {
          System.err.println(reflectResponse);
        }

        @Override
        public void onError(Throwable throwable) {
          latchSad.countDown();
        }

        @Override
        public void onCompleted() {
        }
      });
      Assert.assertTrue(latchHappy.await(15000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(latchSad.await(15000, TimeUnit.MILLISECONDS));
    }

  }

  @Test
  public void trivial_onCompleted() throws Exception {
    try (HandlerBed bed = new HandlerBed(BAD_CODE)) {
      bed.upstream.onCompleted();
    }
  }

  @Test
  public void trivial_error() throws Exception {
    try (HandlerBed bed = new HandlerBed(BAD_CODE)) {
      bed.upstream.onError(new NullPointerException());
    }
  }

}
