/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.sm;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.contracts.AskAttachmentCallback;
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.LatchedSeqCallback;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionActionQueueTests {
  @Test
  public void validateQueueLazyQueueExecutionAndRejection() throws Exception {
    TestBed[] servers = new TestBed[2];
    SimpleExecutor fauxExector = SimpleExecutor.create("routing");
    SlowSingleThreadedExecutorFactory finderExecutor =
        new SlowSingleThreadedExecutorFactory("finder");
    SlowSingleThreadedExecutorFactory directExector =
        new SlowSingleThreadedExecutorFactory("direct");
    SlowSingleThreadedExecutorFactory connectionExecutor =
        new SlowSingleThreadedExecutorFactory("connection");
    ExceptionLogger logger = (t, c) -> {};
    try {
      for (int k = 0; k < servers.length; k++) {
        servers[k] =
            new TestBed(
                21005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");

        CountDownLatch latchMade = new CountDownLatch(1);
        servers[k].coreService.create(
            NtClient.NO_ONE,
            new Key("space", "key"),
            "{}",
            null,
            new Callback<Void>() {
              @Override
              public void success(Void value) {
                latchMade.countDown();
              }

              @Override
              public void failure(ErrorCodeException ex) {}
            });
        Assert.assertTrue(latchMade.await(1000, TimeUnit.MILLISECONDS));
        servers[k].startServer();
      }
      // The faux engine absorbs the workload from the finder
      RoutingEngine fauxEngine =
          new RoutingEngine(fauxExector, new MockSpaceTrackingEvents(), 50, 25);
      // we use the direct engine to control the connection... directly
      RoutingEngine engineDirect =
          new RoutingEngine(directExector, new MockSpaceTrackingEvents(), 50, 25);
      InstanceClientFinder finder =
          new InstanceClientFinder(servers[0].identity, finderExecutor, 2, fauxEngine, logger);
      try {
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsConnected = events.latchAt(1);
        Runnable eventsProducedData = events.latchAt(2);
        Runnable eventsGotUpdate = events.latchAt(3);
        Runnable eventsGotRollback = events.latchAt(4);

        Runnable integrate = directExector.latchAtAndDrain(1, 1);
        Runnable integrateBroadcast = directExector.latchAtAndDrain(2, 1);
        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 20);
        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable gotTargetAndCancel = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable gotFindRequest = finderExecutor.latchAtAndDrain(1, 1);
        Runnable clientSetup = finderExecutor.latchAtAndDrain(2, 1);
        Runnable executeFound = finderExecutor.latchAtAndDrain(3, 1);
        Runnable clientConnected = finderExecutor.latchAtAndDrain(4, 1);
        Runnable clientFound = connectionExecutor.latchAtAndDrain(5, 1);
        Runnable sendConnect = finderExecutor.latchAtAndDrain(5, 1);
        Runnable connectEstablish = finderExecutor.latchAtAndDrain(6, 1);
        Runnable connectionComplete = connectionExecutor.latchAtAndDrain(6, 1);
        Runnable sends = finderExecutor.latchAtAndDrain(23, 17);
        Runnable results = finderExecutor.latchAtAndDrain(39, 16);
        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        engineDirect.integrate("127.0.0.1:21005", Collections.singleton("space"));
        integrate.run();
        integrateBroadcast.run();
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        ArrayList<LatchedSeqCallback> successes = new ArrayList<>();
        for (int k = 0; k < 4; k++) {
          LatchedSeqCallback cb1 = new LatchedSeqCallback();
          LatchedSeqCallback cb2 = new LatchedSeqCallback();
          LatchedSeqCallback cb3 = new LatchedSeqCallback();
          LatchedSeqCallback cb4 = new LatchedSeqCallback();
          connection.send("foo", null, "{\"z\":100}", cb1);
          connection.canAttach(
              new AskAttachmentCallback() {
                @Override
                public void allow() {
                  cb2.success(100);
                }

                @Override
                public void reject() {
                  cb2.success(200);
                }

                @Override
                public void error(int code) {
                  cb2.error(code);
                }
              });
          connection.attach("id", "name", "type", 12, "md5", "sha", cb3);
          connection.send("foo", null, "{\"z\":100}", cb4);
          successes.add(cb1);
          successes.add(cb2);
          successes.add(cb3);
          successes.add(cb4);
        }
        LatchedSeqCallback cbFailure1 = new LatchedSeqCallback();
        connection.send("foo", null, "{\"z\":100}", cbFailure1);
        LatchedSeqCallback cbFailure2 = new LatchedSeqCallback();
        connection.canAttach(
            new AskAttachmentCallback() {
              @Override
              public void allow() {
                cbFailure2.success(100);
              }

              @Override
              public void reject() {
                cbFailure2.success(200);
              }

              @Override
              public void error(int code) {
                cbFailure2.error(code);
              }
            });
        LatchedSeqCallback cbFailure3 = new LatchedSeqCallback();
        connection.attach("id", "name", "type", 12, "md5", "sha", cbFailure3);

        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        eventsConnected.run();
        events.assertWrite(0, "CONNECTED");
        cbFailure1.assertFail(916520);
        cbFailure2.assertFail(901163);
        cbFailure3.assertFail(913447);
        subscribed.run();
        gotTargetAndCancel.run();
        gotFindRequest.run();
        clientSetup.run();
        executeFound.run();
        clientConnected.run();
        clientFound.run();
        sendConnect.run();
        connectEstablish.run();
        connectionComplete.run();
        sends.run();
        results.run();
        eventsProducedData.run();
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        eventsGotUpdate.run();
        System.err.println("SURVEY");
        directExector.survey();
        finderExecutor.survey();
        connectionExecutor.survey();
      } finally {
        System.err.println("FIN\n");
        finder.shutdown();
      }
    } finally {
      for (int k = 0; k < servers.length; k++) {
        if (servers[k] != null) {
          servers[k].close();
        }
      }
      fauxExector.shutdown();
    }
  }
}
