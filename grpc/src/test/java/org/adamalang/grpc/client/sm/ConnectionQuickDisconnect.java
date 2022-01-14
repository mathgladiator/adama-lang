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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionQuickDisconnect {
  @Test
  public void validateNoLeakage() throws Exception {
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
                17005 + k,
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
        Runnable latch = events.latchAt(2);
        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 2);
        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable initialized = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable unsubscribe = directExector.latchAtAndDrain(2, 1);
        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        connection.close();
        ranStart.run();
        subscribed.run();
        initialized.run();
        unsubscribe.run();
        latch.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DISCONNECTED");
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
