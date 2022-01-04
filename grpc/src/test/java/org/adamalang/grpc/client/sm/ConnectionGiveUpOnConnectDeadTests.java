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
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class ConnectionGiveUpOnConnectDeadTests {

  @Test
  public void validateDeadHostWillGiveUp() throws Exception {
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
                15005 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
      }
      // The faux engine absorbs the workload from the finder
      RoutingEngine fauxEngine =
          new RoutingEngine(fauxExector, new MockSpaceTrackingEvents(), 50, 25);
      // we use the direct engine to control the connection... directly
      RoutingEngine engineDirect =
          new RoutingEngine(directExector, new MockSpaceTrackingEvents(), 50, 25);
      InstanceClientFinder finder =
          new InstanceClientFinder(servers[0].identity, finderExecutor, 2, fauxEngine, logger) {
            @Override
            public void find(String target, QueueAction<InstanceClient> action) {
              // MachineIdentity identity,
              //      String target,
              //      SimpleExecutor executor,
              //      Lifecycle lifecycle,
              //      ExceptionLogger logger
              try {
                action.execute(
                    new InstanceClient(
                        servers[0].identity,
                        target,
                        new SimpleExecutor() {
                          @Override
                          public void execute(Runnable command) {}

                          @Override
                          public void schedule(Runnable command, long milliseconds) {}

                          @Override
                          public CountDownLatch shutdown() {
                            return null;
                          }
                        },
                        new Lifecycle() {
                          @Override
                          public void connected(InstanceClient client) {}

                          @Override
                          public void heartbeat(InstanceClient client, Collection<String> spaces) {}

                          @Override
                          public void disconnected(InstanceClient client) {}
                        },
                        (t, c) -> {}) {
                      @Override
                      public long connect(
                          String agent, String authority, String space, String key, Events events) {
                        events.disconnected();
                        return 0;
                      }
                    });
              } catch (Exception ex) {
              }
            }
          };
      try {
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable fin = events.latchAt(2);

        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable gotNewTarget = directExector.latchAtAndDrain(2, 1);
        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribe = directExector.latchAtAndDrain(3, 1);
        Runnable gotInitial = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable[] drive = new Runnable[100];
        for (int k = 0; k < drive.length; k++) {
          drive[k] = connectionExecutor.latchAtAndDrain(1 + k, 1);
        }
        Runnable unsubscribe = directExector.latchAtAndDrain(4, 1);
        engineDirect.integrate("127.0.0.1:15005", Collections.singleton("space"));
        subscribed.run();
        gotNewTarget.run();
        ConnectionBase base = new ConnectionBase(engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        subscribe.run();
        gotInitial.run();
        for (int j = 0; j < drive.length; j++) {
          drive[j].run();
          if ("state=Failed".equals(connection.toString())) {
            unsubscribe.run();
            drive[j].run();
            break;
          }
        }
        fin.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "ERROR:947263");
        System.err.println("SURVEY");
        Thread.sleep(2000);
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
