/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.sm;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClientFinder;
import org.adamalang.net.client.TestClientConfig;
import org.adamalang.net.client.routing.MockSpaceTrackingEvents;
import org.adamalang.net.client.routing.reactive.ReativeRoutingEngine;
import org.adamalang.net.mocks.MockSimpleEvents;
import org.adamalang.net.mocks.SelectiveExecutorFactory;
import org.junit.Test;

import java.util.Collections;

public class ConnectionRoutingFluxTests {
  @Test
  public void cancelFindingClientAfterClientFound() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        Runnable waitForFinderAsk = finderExecutor.pauseOn("finder-find/127.0.0.1:" + servers[0].port);
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        Runnable waitForFoundTargetNull = connectionExecutor.pauseOn("connection-found-local-machine/null");
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        waitForFinderAsk.run();
        Runnable waitForFinderReady = finderExecutor.pauseOn("channel-client-ready");
        engineReal.integrate("127.0.0.1:20005", Collections.emptyList());
        waitForFoundTargetNull.run();
        connectionExecutor.once();
        finderExecutor.unpause();
        connectionExecutor.unpause();
        waitForFinderReady.run();
        finderExecutor.unpause();
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        eventsProducedData.run();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

  @Test
  public void switchTargetForClientAfterClientFound() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        Runnable waitForFinderAsk = finderExecutor.pauseOn("finder-find/127.0.0.1:" + servers[0].port);
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port, "127.0.0.1:" + servers[1].port));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        Runnable waitForFoundTargetNew = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[1].port);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        waitForFinderAsk.run();
        Runnable waitForFinderReady = finderExecutor.pauseOn("channel-client-ready");
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.singleton("space"));
        waitForFoundTargetNew.run();
        connectionExecutor.once();
        finderExecutor.unpause();
        connectionExecutor.unpause();
        waitForFinderReady.run();
        finderExecutor.unpause();
        eventsProducedData.run();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

  @Test
  public void failedFinding() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf());
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        eventsProducedData.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "ERROR:992319");
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

  @Test
  public void failedFindingButNewTargetThenNuke() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf());
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        Runnable waitForFind = finderExecutor.pauseOn("finder-find/127.0.0.1:" + servers[0].port);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        finder.sync(Helper.setOf());

        // swap out the server to change targets
        waitForFind.run();
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.emptyList());
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.singleton("space"));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[1].port);
        Runnable waitForRoutingUpdate = connectionExecutor.pauseOn("connection-failed-retry");
        Runnable waitForFindAgain = finderExecutor.pauseOn("finder-find/127.0.0.1:" + servers[1].port);
        finderExecutor.unpause();
        waitForRoutingUpdate.run();
        Runnable waitForNullTarget = connectionExecutor.pauseOn("connection-found-local-machine/null");
        connectionExecutor.unpause();
        waitForFindAgain.run();
        System.err.println("--waiting to find again");

        // nuke the new server
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.emptyList());
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", null);
        waitForNullTarget.run();
        connectionExecutor.unpause();
        connectionExecutor.flush();

        System.err.println("--got-null-target");

        Runnable waitForRoutingUpdateAgain = connectionExecutor.pauseOn("connection-failed-retry");
        finderExecutor.unpause();
        waitForRoutingUpdateAgain.run();
        connectionExecutor.unpause();
        connectionExecutor.flush();

        connection.close();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

  @Test
  public void closeBeforeFindingFailure() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        Runnable waitForFinderAsk = finderExecutor.pauseOn("finder-find/127.0.0.1:" + servers[0].port);
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        Runnable waitForFoundTargetNull = connectionExecutor.pauseOn("connection-close/space/key");
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        waitForFinderAsk.run();
        connection.close();
        waitForFoundTargetNull.run();
        connectionExecutor.once();
        finderExecutor.unpause();
        Runnable waitForFailure = connectionExecutor.pauseOn("connection-failed-finding-client/719932");
        connectionExecutor.unpause();
        waitForFailure.run();
        connectionExecutor.unpause();
        eventsProducedData.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DISCONNECTED");
        connection.close();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

  @Test
  public void cancelRoutingAfterFoundClient() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        Runnable waitForConnection = connectionExecutor.pauseOn("connection-connected");
        connection.open();
        waitForConnection.run();
        Runnable extracted = connectionExecutor.extract();
        Runnable waitForFoundTargetNull = connectionExecutor.pauseOn("connection-found-local-machine/null");
        connectionExecutor.unpause();
        engineReal.integrate("127.0.0.1:20005", Collections.emptyList());
        waitForFoundTargetNull.run();
        Runnable waitForDisconnect = connectionExecutor.pauseOn("connection-disconnected");
        connectionExecutor.unpause();
        connectionExecutor.flush();
        extracted.run();
        waitForDisconnect.run();
        Runnable waitForFoundNewTarget = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[0].port);
        connectionExecutor.unpause();
        connectionExecutor.flush();
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        waitForFoundNewTarget.run();
        connectionExecutor.unpause();
        eventsProducedData.run();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

  @Test
  public void routingFluxAfterFoundClient() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(3);
        Runnable reconnected = events.latchAt(4);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        Runnable waitForConnection = connectionExecutor.pauseOn("connection-connected");
        connection.open();
        waitForConnection.run();
        Runnable extracted = connectionExecutor.extract();
        Runnable waitForFoundTargetNull = connectionExecutor.pauseOn("connection-found-local-machine/null");
        connectionExecutor.unpause();
        engineReal.integrate("127.0.0.1:20005", Collections.emptyList());
        waitForFoundTargetNull.run();
        Runnable waitForDisconnect = connectionExecutor.pauseOn("connection-disconnected");
        connectionExecutor.unpause();
        connectionExecutor.flush();
        extracted.run();
        waitForDisconnect.run();
        extracted = connectionExecutor.extract();
        Runnable waitForFoundNewTarget = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[0].port);
        connectionExecutor.unpause();
        connectionExecutor.flush();
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        waitForFoundNewTarget.run();
        connectionExecutor.unpause();
        Runnable retryLoop = connectionExecutor.pauseOn("connection-retry");
        extracted.run();
        retryLoop.run();
        Runnable connected = connectionExecutor.pauseOn("connection-connected");
        connectionExecutor.unpause();
        connected.run();
        connectionExecutor.unpause();
        eventsProducedData.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":123},\"seq\":9}");
        Runnable killConnection = connectionExecutor.pauseOn("connection-found-local-machine/null");
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.emptyList());
        killConnection.run();
        connectionExecutor.once();
        connectionExecutor.flush();
        Runnable waitForDisconnect2 = connectionExecutor.pauseOn("connection-disconnected");
        connectionExecutor.unpause();
        waitForDisconnect2.run();
        Runnable executeDisconnect = connectionExecutor.extract();

        Runnable waitAgainForTarget = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[0].port);
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        connectionExecutor.unpause();
        waitAgainForTarget.run();
        executeDisconnect.run();
        connectionExecutor.unpause();

        reconnected.run();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }


  @Test
  public void newTargetWhileDisconnect() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SelectiveExecutorFactory routingExecutor = new SelectiveExecutorFactory("routing");
    SelectiveExecutorFactory connectionExecutor = new SelectiveExecutorFactory("connection");
    SelectiveExecutorFactory finderExecutor = new SelectiveExecutorFactory("finder");
    ExceptionLogger logger = (t, c) -> {};
    try {
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      TestClientConfig clientConfig = new TestClientConfig();
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engineReal = new ReativeRoutingEngine(metrics, routingExecutor, new MockSpaceTrackingEvents(), 5, 5);
      ReativeRoutingEngine engineFaux = new ReativeRoutingEngine(metrics, SimpleExecutor.NOW, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, finderExecutor, 2, engineFaux, logger);
      try {
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port, "127.0.0.1:" + servers[1].port));
        ComplexHelper.waitForRoutingToCatch(engineReal, "space", "key", "127.0.0.1:" + servers[0].port);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engineReal, finder, connectionExecutor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable firstConnection = events.latchAt(2);
        Runnable secondConnection = events.latchAt(3);
        Connection connection = new Connection(base, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        firstConnection.run();
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.singleton("space"));
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.emptyList());
        secondConnection.run();
        Runnable waitForDisconnect = connectionExecutor.pauseOn("connection-disconnected");
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.emptyList());
        waitForDisconnect.run();
        Runnable executeDisconnect = connectionExecutor.extract();
        connectionExecutor.unpause();
        Runnable waitAgainForTarget1 = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[0].port);
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        waitAgainForTarget1.run();
        Runnable waitAgainForTarget2 = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[1].port);
        connectionExecutor.unpause();
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.singleton("space"));
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.emptyList());
        waitAgainForTarget2.run();

        Runnable waitAgainForNullTarget = connectionExecutor.pauseOn("connection-found-local-machine/null");
        engineReal.integrate("127.0.0.1:" + servers[1].port, Collections.emptyList());
        waitAgainForNullTarget.run();
        connectionExecutor.unpause();

        Runnable waitAgainForTarget3 = connectionExecutor.pauseOn("connection-found-local-machine/127.0.0.1:" + servers[0].port);
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.singleton("space"));
        waitAgainForTarget3.run();
        connectionExecutor.unpause();

        Runnable waitAgainForTarget4 = connectionExecutor.pauseOn("connection-found-local-machine/null");
        engineReal.integrate("127.0.0.1:" + servers[0].port, Collections.emptyList());
        waitAgainForTarget4.run();
        connectionExecutor.unpause();
        connectionExecutor.flush();
        executeDisconnect.run();
        connectionExecutor.flush();
      } finally {
        System.err.println("FIN");
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      routingExecutor.shutdown();
      connectionExecutor.shutdown();
      finderExecutor.shutdown();
    }
  }

}
