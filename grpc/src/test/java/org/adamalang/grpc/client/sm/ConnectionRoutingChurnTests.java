package org.adamalang.grpc.client.sm;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.SimpleExecutorFactory;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.MockEvents;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionRoutingChurnTests {
  @Test
  public void manualSelectorTest() throws Exception {
    TestBed[] servers = new TestBed[2];
    SimpleExecutor fauxExector = SimpleExecutor.create("routing");
    SlowSingleThreadedExecutorFactory finderExecutor = new SlowSingleThreadedExecutorFactory("finder");
    SlowSingleThreadedExecutorFactory directExector = new SlowSingleThreadedExecutorFactory("direct");
    SlowSingleThreadedExecutorFactory connectionExecutor = new SlowSingleThreadedExecutorFactory("connection");
    ExceptionLogger logger = (t, c) -> {};
    try {
      for (int k = 0; k < servers.length; k++) {
        servers[k] = new TestBed(20005 + k, "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
        servers[k].startServer();
      }
      // The faux engine absorbs the workload from the finder
      RoutingEngine fauxEngine = new RoutingEngine(fauxExector, new MockSpaceTrackingEvents(), 50, 25);
      // we use the direct engine to control the connection... directly
      RoutingEngine engineDirect = new RoutingEngine(directExector, new MockSpaceTrackingEvents(), 50, 25);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].identity, finderExecutor, 2, fauxEngine, logger);
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
        System.err.println("OK, now unleash the finder");
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
      } finally{
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
