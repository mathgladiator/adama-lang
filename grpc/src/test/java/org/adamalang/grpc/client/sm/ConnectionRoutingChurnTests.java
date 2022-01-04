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
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.LatchedSeqCallback;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionRoutingChurnTests {
  @Test
  public void manualSelectorTest() throws Exception {
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
                20005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
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
        Runnable targetChange = directExector.latchAtAndDrain(5, 1);
        Runnable broadcastNewTarget = directExector.latchAtAndDrain(6, 2);
        Runnable executeFound = finderExecutor.latchAtAndDrain(2, 1);
        Runnable clientConnected = finderExecutor.latchAtAndDrain(3, 1);
        Runnable clientEstablished = finderExecutor.latchAtAndDrain(4, 1);
        Runnable gotChangedTarget = connectionExecutor.latchAtAndDrain(5, 1);
        Runnable foundClient = connectionExecutor.latchAtAndDrain(6, 1);
        Runnable targetLost = directExector.latchAtAndDrain(7, 1);
        Runnable broadcastLost = directExector.latchAtAndDrain(8, 1);
        Runnable clientGotBroadcast = connectionExecutor.latchAtAndDrain(7, 1);
        Runnable findIssued = finderExecutor.latchAtAndDrain(5, 1);
        Runnable executeFoundAgain = finderExecutor.latchAtAndDrain(6, 1);
        Runnable clientConnectedAgain = finderExecutor.latchAtAndDrain(7, 1);
        Runnable clientEstablishedAgain = finderExecutor.latchAtAndDrain(8, 1);
        Runnable foundClientAgain = connectionExecutor.latchAtAndDrain(8, 1);
        Runnable integrateFinalHost = directExector.latchAtAndDrain(9, 1);
        Runnable executeDisconnect = connectionExecutor.latchAtAndDrain(9, 1);
        Runnable broadcastFinalGain = directExector.latchAtAndDrain(10, 1);
        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        subscribed.run();
        gotNullTargetAndCancel.run();
        engineDirect.integrate("127.0.0.1:20005", Collections.singleton("space"));
        gotNewTarget.run();
        newTargetBroadcastQueued.run();
        gotFirstTarget.run();
        gotFindRequest.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        engineDirect.integrate("127.0.0.1:20006", Collections.singleton("space"));
        engineDirect.integrate("127.0.0.1:20005", Collections.emptyList());
        targetChange.run();
        broadcastNewTarget.run();
        executeFound.run();
        clientConnected.run();
        clientEstablished.run();
        gotChangedTarget.run();
        foundClient.run();
        engineDirect.integrate("127.0.0.1:20006", Collections.emptyList());
        targetLost.run();
        broadcastLost.run();
        clientGotBroadcast.run();
        Assert.assertEquals("state=FindingClientCancelStop", connection.toString());
        findIssued.run();
        executeFoundAgain.run();
        clientConnectedAgain.run();
        clientEstablishedAgain.run();
        foundClientAgain.run();
        Assert.assertEquals("state=NotConnected", connection.toString());
        engineDirect.integrate("127.0.0.1:20005", Collections.singleton("space"));
        integrateFinalHost.run();
        connection.close();
        executeDisconnect.run();
        broadcastFinalGain.run();
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

  @Test
  public void failOverTest() throws Exception {
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
                20005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");

        CountDownLatch latchMade = new CountDownLatch(1);
        servers[k].coreService.create(NtClient.NO_ONE, new Key("space", "key"), "{}", null, new Callback<Void>() {
          @Override
          public void success(Void value) {
            latchMade.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
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

        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable gotNullTargetAndCancel = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable gotNewTarget = directExector.latchAtAndDrain(2, 1);
        Runnable newTargetBroadcastQueued = directExector.latchAtAndDrain(3, 1);
        Runnable gotFirstTarget = connectionExecutor.latchAtAndDrain(4, 1);
        Runnable gotFindRequest = finderExecutor.latchAtAndDrain(1, 1);
        Runnable clientSetup = finderExecutor.latchAtAndDrain(2, 1);
        Runnable executeFound = finderExecutor.latchAtAndDrain(3, 1);
        Runnable clientConnected = finderExecutor.latchAtAndDrain(4, 1);
        Runnable clientFound = connectionExecutor.latchAtAndDrain(5, 1);
        Runnable clientReconnecting = finderExecutor.latchAtAndDrain(5, 1);
        Runnable clientGotEstablished = finderExecutor.latchAtAndDrain(6, 1);
        Runnable clientMade = finderExecutor.latchAtAndDrain(7, 1);
        Runnable connectionMade = connectionExecutor.latchAtAndDrain(6, 1);
        Runnable executeSend = connectionExecutor.latchAtAndDrain(7, 1);
        Runnable forwardSend = finderExecutor.latchAtAndDrain(8, 1);
        Runnable clientDataForward = finderExecutor.latchAtAndDrain(9, 1);
        Runnable sendSeqResult = finderExecutor.latchAtAndDrain(10, 1);
        Runnable gotDisconnect = finderExecutor.latchAtAndDrain(11, 1);
        Runnable connectionBroke = connectionExecutor.latchAtAndDrain(8, 1);
        Runnable connectionRetry = connectionExecutor.latchAtAndDrain(9, 1);
        Runnable finderRetry = finderExecutor.latchAtAndDrain(13, 2);
        Runnable finderFailed = finderExecutor.latchAtAndDrain(14, 1);
        Runnable finderFailedAgain = finderExecutor.latchAtAndDrain(15, 1);
        Runnable executorRemoves = directExector.latchAtAndDrain(4, 1);
        Runnable finderSyncsX = finderExecutor.latchAtAndDrain(16, 1);
        Runnable finderSyncsY = finderExecutor.latchAtAndDrain(17, 1);
        Runnable finderSyncsZ = finderExecutor.latchAtAndDrain(18, 1);
        Runnable finderCleansUp = finderExecutor.latchAtAndDrain(21, 3);
        Runnable broadcastNewTarget = directExector.latchAtAndDrain(5, 1);
        Runnable completeClients = finderExecutor.latchAtAndDrain(23, 2);
        Runnable newConnectionAvailable = finderExecutor.latchAtAndDrain(24, 1);
        Runnable newConnectionEstablished = finderExecutor.latchAtAndDrain(25, 1);
        Runnable connectionFoundTarget = connectionExecutor.latchAtAndDrain(10, 1);
        Runnable newConnectionSignal = connectionExecutor.latchAtAndDrain(11, 1);
        Runnable findNewTargetForFailOver = finderExecutor.latchAtAndDrain(26, 1);
        Runnable foundConnection = connectionExecutor.latchAtAndDrain(12, 1);
        Runnable executeConnect = finderExecutor.latchAtAndDrain(27, 1);
        Runnable produceEventsOnNewDataStream = finderExecutor.latchAtAndDrain(29, 2);
        Runnable connectionComplete = connectionExecutor.latchAtAndDrain(13, 1);

        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        eventsConnected.run();
        events.assertWrite(0, "CONNECTED");
        subscribed.run();
        gotNullTargetAndCancel.run();
        engineDirect.integrate("127.0.0.1:20005", Collections.singleton("space"));
        engineDirect.integrate("127.0.0.1:20006", Collections.singleton("space"));
        gotNewTarget.run();
        newTargetBroadcastQueued.run();
        gotFirstTarget.run();
        gotFindRequest.run();
        clientSetup.run();
        executeFound.run();
        clientConnected.run();
        clientFound.run();
        clientReconnecting.run();
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        clientGotEstablished.run();
        clientMade.run();
        eventsProducedData.run();
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        connectionMade.run();
        Assert.assertEquals("state=Connected", connection.toString());
        LatchedSeqCallback cb1 = new LatchedSeqCallback();
        connection.send("foo", null, "{\"z\":100}", cb1);
        executeSend.run();
        forwardSend.run();
        clientDataForward.run();
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":223},\"seq\":6}");
        sendSeqResult.run();
        eventsGotUpdate.run();
        cb1.assertSuccess(6);
        servers[1].stopServer();
        gotDisconnect.run();
        Assert.assertEquals("state=Connected", connection.toString());
        connectionBroke.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        connectionRetry.run();
        finderRetry.run();
        finderFailed.run();
        finderFailedAgain.run();
        engineDirect.remove("127.0.0.1:20006");
        finder.sync(new TreeSet<>(Collections.singleton("127.0.0.1:20005")));
        executorRemoves.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        finderSyncsX.run();
        finderSyncsY.run();
        finderSyncsZ.run();
        finderCleansUp.run();
        broadcastNewTarget.run();
        completeClients.run();
        newConnectionAvailable.run();
        newConnectionEstablished.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        connectionFoundTarget.run();
        Assert.assertEquals("state=FindingClientCancelTryNewTarget", connection.toString());
        newConnectionSignal.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        findNewTargetForFailOver.run();
        foundConnection.run();
        executeConnect.run();
        produceEventsOnNewDataStream.run();
        eventsGotRollback.run();
        connectionComplete.run();
        events.assertWrite(3, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        Assert.assertEquals("state=Connected", connection.toString());
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

  @Test
  public void trafficShift() throws Exception {
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
                20005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");

        CountDownLatch latchMade = new CountDownLatch(1);
        servers[k].coreService.create(NtClient.NO_ONE, new Key("space", "key"), "{}", null, new Callback<Void>() {
          @Override
          public void success(Void value) {
            latchMade.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
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

        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable gotNullTargetAndCancel = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable gotNewTarget = directExector.latchAtAndDrain(2, 1);
        Runnable newTargetBroadcastQueued = directExector.latchAtAndDrain(3, 1);
        Runnable gotFirstTarget = connectionExecutor.latchAtAndDrain(4, 1);
        Runnable gotFindRequest = finderExecutor.latchAtAndDrain(1, 1);
        Runnable clientSetup = finderExecutor.latchAtAndDrain(2, 1);
        Runnable executeFound = finderExecutor.latchAtAndDrain(3, 1);
        Runnable clientConnected = finderExecutor.latchAtAndDrain(4, 1);
        Runnable clientFound = connectionExecutor.latchAtAndDrain(5, 1);
        Runnable clientReconnecting = finderExecutor.latchAtAndDrain(5, 1);
        Runnable clientGotEstablished = finderExecutor.latchAtAndDrain(6, 1);
        Runnable clientMade = finderExecutor.latchAtAndDrain(7, 1);
        Runnable connectionMade = connectionExecutor.latchAtAndDrain(6, 1);
        Runnable executeSend = connectionExecutor.latchAtAndDrain(7, 1);
        Runnable forwardSend = finderExecutor.latchAtAndDrain(8, 1);
        Runnable clientDataForward = finderExecutor.latchAtAndDrain(9, 1);
        Runnable sendSeqResult = finderExecutor.latchAtAndDrain(10, 1);
        Runnable executorIntegrates = directExector.latchAtAndDrain(4, 1);
        Runnable broadcastNewTarget = directExector.latchAtAndDrain(5, 1);
        Runnable connectionFoundNewTarget = connectionExecutor.latchAtAndDrain(8, 1);
        Runnable connectionGetsClosed = finderExecutor.latchAtAndDrain(11, 1);
        Runnable disconnectStatusShared = finderExecutor.latchAtAndDrain(12, 1);
        Runnable connectionConfirmsDisconnect = connectionExecutor.latchAtAndDrain(9, 1);
        Runnable connectionRetry = connectionExecutor.latchAtAndDrain(10, 1);
        Runnable finderRetry = finderExecutor.latchAtAndDrain(13, 2);
        Runnable finderSuccess = finderExecutor.latchAtAndDrain(14, 1);
        Runnable finderEstablished = finderExecutor.latchAtAndDrain(15, 1);
        Runnable finderSendsFound = finderExecutor.latchAtAndDrain(16, 1);
        Runnable connectionGetsClient = connectionExecutor.latchAtAndDrain(11, 1);
        Runnable executeConnectAgain = finderExecutor.latchAtAndDrain(17, 1);
        Runnable pumpEvents = finderExecutor.latchAtAndDrain(19, 2);
        Runnable completeConnection = connectionExecutor.latchAtAndDrain(12, 2);
        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        eventsConnected.run();
        events.assertWrite(0, "CONNECTED");
        subscribed.run();
        gotNullTargetAndCancel.run();
        engineDirect.integrate("127.0.0.1:20005", Collections.singleton("space"));
        gotNewTarget.run();
        newTargetBroadcastQueued.run();
        gotFirstTarget.run();
        gotFindRequest.run();
        clientSetup.run();
        executeFound.run();
        clientConnected.run();
        clientFound.run();
        clientReconnecting.run();
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        clientGotEstablished.run();
        clientMade.run();
        eventsProducedData.run();
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        connectionMade.run();
        Assert.assertEquals("state=Connected", connection.toString());
        LatchedSeqCallback cb1 = new LatchedSeqCallback();
        connection.send("foo", null, "{\"z\":100}", cb1);
        executeSend.run();
        forwardSend.run();
        clientDataForward.run();
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":223},\"seq\":6}");
        sendSeqResult.run();
        eventsGotUpdate.run();
        cb1.assertSuccess(6);
        engineDirect.integrate("127.0.0.1:20006", Collections.singleton("space"));
        executorIntegrates.run();
        broadcastNewTarget.run();
        Assert.assertEquals("state=Connected", connection.toString());
        connectionFoundNewTarget.run();
        connectionGetsClosed.run();
        disconnectStatusShared.run();
        Assert.assertEquals("state=Connected", connection.toString());
        connectionConfirmsDisconnect.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        connectionRetry.run();
        finderRetry.run();
        finderSuccess.run();
        finderEstablished.run();
        finderSendsFound.run();
        connectionGetsClient.run();
        executeConnectAgain.run();
        pumpEvents.run();
        eventsGotRollback.run();
        events.assertWrite(3, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        completeConnection.run();
        Assert.assertEquals("state=Connected", connection.toString());
        Thread.sleep(1000);
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
