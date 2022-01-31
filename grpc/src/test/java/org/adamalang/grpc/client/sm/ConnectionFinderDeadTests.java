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

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.ClientMetrics;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class ConnectionFinderDeadTests {

  @Test
  public void errorIfCantFindForTooLong() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[1];
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
                32005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
      }
      // The faux engine absorbs the workload from the finder
      RoutingEngine fauxEngine =
          new RoutingEngine(metrics, fauxExector, new MockSpaceTrackingEvents(), 50, 25);
      // we use the direct engine to control the connection... directly
      RoutingEngine engineDirect =
          new RoutingEngine(metrics, directExector, new MockSpaceTrackingEvents(), 50, 25);
      InstanceClientFinder finder =
          new InstanceClientFinder(metrics, null, servers[0].identity, finderExecutor, 2, fauxEngine, logger) {
            @Override
            public void find(String target, ItemAction<InstanceClient> action) {
              action.killDueToReject();
            }
          };
      try {
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable fin = events.latchAt(2);
        Runnable gotNewTarget = directExector.latchAtAndDrain(1, 1);
        Runnable newTargetBroadcast = directExector.latchAtAndDrain(2, 1);
        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribed = directExector.latchAtAndDrain(3, 1);
        Runnable gotSubscribe = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable unsub = directExector.latchAtAndDrain(4, 1);

        Runnable[] failed = new Runnable[100];
        for (int j = 0; j < failed.length; j++) {
          failed[j] = connectionExecutor.latchAtAndDrain(4 + j, 1);
        }

        engineDirect.integrate("127.0.0.1:32005", Collections.singleton("space"));

        gotNewTarget.run();
        newTargetBroadcast.run();
        ConnectionBase base = new ConnectionBase(metrics, engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", "{}", events);
        connection.open();
        ranStart.run();
        subscribed.run();
        gotSubscribe.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        for (int j = 0; j < failed.length; j++) {
          failed[j].run();
          if ("state=Failed".equals(connection.toString())) {
            break;
          }
        }
        unsub.run();
        fin.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "ERROR:992319");
        System.err.println("SURVEY");
        directExector.survey();
        connectionExecutor.survey();
        finderExecutor.survey();
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
  public void stopWhenTargetGoes() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[1];
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
                32005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
      }
      // The faux engine absorbs the workload from the finder
      RoutingEngine fauxEngine =
          new RoutingEngine(metrics, fauxExector, new MockSpaceTrackingEvents(), 50, 25);
      // we use the direct engine to control the connection... directly
      RoutingEngine engineDirect =
          new RoutingEngine(metrics, directExector, new MockSpaceTrackingEvents(), 50, 25);
      InstanceClientFinder finder =
          new InstanceClientFinder(metrics, null, servers[0].identity, finderExecutor, 2, fauxEngine, logger) {
            @Override
            public void find(String target, ItemAction<InstanceClient> action) {
              action.killDueToReject();
            }
          };
      try {
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable gotNewTarget = directExector.latchAtAndDrain(1, 1);
        Runnable newTargetBroadcast = directExector.latchAtAndDrain(2, 1);
        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribed = directExector.latchAtAndDrain(3, 1);
        Runnable gotSubscribe = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable removeTarget = directExector.latchAtAndDrain(4, 1);
        Runnable removeTargetBroadcast = directExector.latchAtAndDrain(5, 1);
        Runnable[] failed = new Runnable[6];
        for (int j = 0; j < failed.length; j++) {
          failed[j] = connectionExecutor.latchAtAndDrain(4 + j, 1);
        }
        Runnable gotRemoval = connectionExecutor.latchAtAndDrain(11, 2);
        Runnable doneHappens = connectionExecutor.latchAtAndDrain(12, 1);

        engineDirect.integrate("127.0.0.1:32005", Collections.singleton("space"));
        gotNewTarget.run();
        newTargetBroadcast.run();
        ConnectionBase base = new ConnectionBase(metrics, engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", "{}", events);
        connection.open();
        ranStart.run();
        subscribed.run();
        gotSubscribe.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        for (int j = 0; j < failed.length; j++) {
          failed[j].run();
        }
        engineDirect.remove("127.0.0.1:32005");
        removeTarget.run();
        removeTargetBroadcast.run();
        gotRemoval.run();
        Assert.assertEquals("state=FindingClientCancelStop", connection.toString());
        doneHappens.run();
        Assert.assertEquals("state=NotConnected", connection.toString());
        System.err.println("SURVEY");
        directExector.survey();
        connectionExecutor.survey();
        finderExecutor.survey();
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
