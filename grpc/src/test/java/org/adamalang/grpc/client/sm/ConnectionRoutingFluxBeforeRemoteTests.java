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
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionRoutingFluxBeforeRemoteTests {
  @Test
  public void validateSkipsClientBasedOnTargetChange() throws Exception {
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
                25005 + k,
                "@can_create(who) { return true; } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
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
        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable gotNullTargetAndCancel = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable gotNewTarget = directExector.latchAtAndDrain(2, 1);
        Runnable newTargetBroadcastQueued = directExector.latchAtAndDrain(3, 1);
        Runnable gotFirstTarget = connectionExecutor.latchAtAndDrain(4, 1);
        Runnable gotFindRequest = finderExecutor.latchAtAndDrain(1, 1);
        Runnable executeFound = finderExecutor.latchAtAndDrain(2, 1);
        Runnable targetChange = directExector.latchAtAndDrain(5, 2);
        Runnable clientConnected = finderExecutor.latchAtAndDrain(3, 1);
        Runnable broadcastNewTarget = directExector.latchAtAndDrain(6, 1);
        Runnable clientEstablished = finderExecutor.latchAtAndDrain(4, 1);
        Runnable gotClient = connectionExecutor.latchAtAndDrain(5, 1);
        Runnable gotSecondTarget = connectionExecutor.latchAtAndDrain(6, 1);
        Runnable executeNewConnection = finderExecutor.latchAtAndDrain(5, 1);
        Runnable firstProducedStatus = finderExecutor.latchAtAndDrain(7, 2);
        Runnable registerConnection = connectionExecutor.latchAtAndDrain(7, 1);
        Runnable disconnectFirstStream = finderExecutor.latchAtAndDrain(8, 1);
        Runnable gotDisconnectSignal = finderExecutor.latchAtAndDrain(9, 1);
        Runnable connectionGetsDisconnectSignal = connectionExecutor.latchAtAndDrain(8, 1);
        Runnable connectionRetry = connectionExecutor.latchAtAndDrain(9, 1);
        Runnable findGoesToNew = finderExecutor.latchAtAndDrain(10, 1);
        Runnable setupNew = finderExecutor.latchAtAndDrain(11, 1);
        Runnable newGetsEstablished = finderExecutor.latchAtAndDrain(12, 1);
        Runnable reportFound = finderExecutor.latchAtAndDrain(13, 1);
        Runnable registerFound = connectionExecutor.latchAtAndDrain(10, 1);
        Runnable addConnection = finderExecutor.latchAtAndDrain(13, 1);
        Runnable removeTarget = directExector.latchAtAndDrain(7, 1);
        Runnable broadcastRemoval = directExector.latchAtAndDrain(8, 1);
        Runnable understandRemovalForConnection = connectionExecutor.latchAtAndDrain(11, 1);
        Runnable secondStatus = finderExecutor.latchAtAndDrain(15, 1);
        Runnable secondData = finderExecutor.latchAtAndDrain(16, 1);
        Runnable connectionOver = connectionExecutor.latchAtAndDrain(12, 1);
        Runnable disconnectGoodOne = finderExecutor.latchAtAndDrain(17, 1);
        Runnable finalStatus = finderExecutor.latchAtAndDrain(18, 1);
        Runnable connectionGetsDisconnect = connectionExecutor.latchAtAndDrain(13, 1);

        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        subscribed.run();
        gotNullTargetAndCancel.run();
        engineDirect.integrate("127.0.0.1:25005", Collections.singleton("space"));
        gotNewTarget.run();
        newTargetBroadcastQueued.run();
        gotFirstTarget.run();
        gotFindRequest.run();
        executeFound.run();
        clientConnected.run();
        clientEstablished.run();
        gotClient.run();
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        engineDirect.integrate("127.0.0.1:25006", Collections.singleton("space"));
        engineDirect.integrate("127.0.0.1:25005", Collections.emptyList());
        targetChange.run();
        broadcastNewTarget.run();
        gotSecondTarget.run();
        executeNewConnection.run();
        Assert.assertEquals("state=FoundClientConnectingTryNewTarget", connection.toString());
        firstProducedStatus.run();
        registerConnection.run();
        disconnectFirstStream.run();
        gotDisconnectSignal.run();
        connectionGetsDisconnectSignal.run();
        connectionRetry.run();
        findGoesToNew.run();
        setupNew.run();
        newGetsEstablished.run();
        reportFound.run();
        registerFound.run();
        addConnection.run();
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        engineDirect.remove("127.0.0.1:25006");
        removeTarget.run();
        broadcastRemoval.run();
        understandRemovalForConnection.run();
        Assert.assertEquals("state=FoundClientConnectingStop", connection.toString());
        secondStatus.run();
        secondData.run();
        connectionOver.run();
        disconnectGoodOne.run();
        finalStatus.run();
        connectionGetsDisconnect.run();
        Assert.assertEquals("state=NotConnected", connection.toString());
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
