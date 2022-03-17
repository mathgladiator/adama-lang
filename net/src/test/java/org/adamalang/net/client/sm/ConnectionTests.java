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

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.queue.ItemAction;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.*;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.net.client.contracts.SpaceTrackingEvents;
import org.adamalang.net.client.routing.RoutingEngine;
import org.adamalang.net.mocks.MockSimpleEvents;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionTests {
  @Test
  public void failureToConnect() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    try (TestBed server =
        new TestBed(
            30000,
            "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      server.startServer();
      SimpleExecutor routingExecutor = SimpleExecutor.create("routing");
      try {
        ExceptionLogger logger = (t, c) -> {};
        CountDownLatch primed = new CountDownLatch(1);
        RoutingEngine engine =
            new RoutingEngine(
                metrics,
                routingExecutor,
                new SpaceTrackingEvents() {
                  @Override
                  public void gainInterestInSpace(String space) {
                    System.err.println("gain:" + space);
                  }

                  @Override
                  public void shareTargetsFor(String space, Set<String> targets) {
                    if ("space".equals(space) && targets.size() == 1) {
                      primed.countDown();
                    }
                  }

                  @Override
                  public void lostInterestInSpace(String space) {
                    System.err.println("lost:" + space);
                  }
                },
                50,
                25);
        ClientConfig clientConfig = new TestClientConfig();
        InstanceClientFinder finder = new InstanceClientFinder(server.base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 1, engine, logger);
        finder.sync(new TreeSet<>(Collections.singleton("127.0.0.1:30000")));
        Assert.assertTrue(primed.await(15000, TimeUnit.MILLISECONDS));
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engine, finder, server.clientExecutor);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger codeFound = new AtomicInteger(0);
        new Connection(
                base,
                "origin",
                "me",
                "dev",
                "space",
                "key1", "{}",
                new SimpleEvents() {
                  @Override
                  public void connected() {
                    System.err.println("connected");
                  }

                  @Override
                  public void delta(String data) {
                    System.err.println("data:" + data);
                  }

                  @Override
                  public void error(int code) {
                    System.err.println("error:" + code);
                    codeFound.set(code);
                    latch.countDown();
                  }

                  @Override
                  public void disconnected() {
                    System.err.println("disconnected");
                  }
                })
            .open();
        Assert.assertTrue(latch.await(25000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(625676, codeFound.get());
      } finally {
        routingExecutor.shutdown();
      }
    }
  }

  @Test
  public void connectHappy() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());

    try (TestBed server =
             new TestBed(
                 30001,
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      server.startServer();
      SimpleExecutor routingExecutor = SimpleExecutor.create("routing");
      try {
        ExceptionLogger logger = (t, c) -> {};
        CountDownLatch primed = new CountDownLatch(1);
        RoutingEngine engine =
            new RoutingEngine(
                metrics,
                routingExecutor,
                new SpaceTrackingEvents() {
                  @Override
                  public void gainInterestInSpace(String space) {
                    System.err.println("gain:" + space);
                  }

                  @Override
                  public void shareTargetsFor(String space, Set<String> targets) {
                    if ("space".equals(space) && targets.size() == 1) {
                      primed.countDown();
                    }
                  }

                  @Override
                  public void lostInterestInSpace(String space) {
                    System.err.println("lost:" + space);
                  }
                },
                50,
                25);
        ClientConfig clientConfig = new TestClientConfig();
        InstanceClientFinder finder =
            new InstanceClientFinder(server.base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 1, engine, logger);
        finder.sync(new TreeSet<>(Collections.singleton("127.0.0.1:30001")));
        Assert.assertTrue(primed.await(15000, TimeUnit.MILLISECONDS));
        CountDownLatch created = new CountDownLatch(1);
        finder.find("127.0.0.1:30001", new Callback<InstanceClient>() {
          @Override
          public void success(InstanceClient client) {
            System.err.println("executing create");
            client.create("origin", "me", "dev", "space", "key1", null, "{}", new Callback<Void>() {
              @Override
              public void success(Void value) {
                System.err.println("create happy");
                created.countDown();
              }

              @Override
              public void failure(ErrorCodeException ex) {
                Assert.fail();

              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
          }
        });
        Assert.assertTrue(created.await(1000, TimeUnit.MILLISECONDS));
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engine, finder, server.clientExecutor);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latchD = new CountDownLatch(1);
        Connection connection =
            new Connection(
                base,
                "origin",
                "me",
                "dev",
                "space",
                "key1", "{}",
                new SimpleEvents() {
                  @Override
                  public void connected() {
                    System.err.println("connected");
                  }

                  @Override
                  public void delta(String data) {
                    System.err.println("data:" + data);
                    latch.countDown();
                  }

                  @Override
                  public void error(int code) {
                    System.err.println("error:" + code);
                  }

                  @Override
                  public void disconnected() {
                    System.err.println("disconnected");
                    latchD.countDown();
                  }
                });
        connection.open();
        Assert.assertTrue(latch.await(25000, TimeUnit.MILLISECONDS));
        connection.close();
        Assert.assertTrue(latchD.await(25000, TimeUnit.MILLISECONDS));
      } finally {
        routingExecutor.shutdown();
      }
    }
  }

  @Test
  public void openClose() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());

    try (TestBed server =
             new TestBed(
                 30001,
                 "@static { create(who) { return true; } } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
      server.startServer();
      SimpleExecutor routingExecutor = SimpleExecutor.create("routing");
      try {
        ClientConfig clientConfig = new TestClientConfig();
        ExceptionLogger logger = (t, c) -> {};
        CountDownLatch primed = new CountDownLatch(1);
        RoutingEngine engine =
            new RoutingEngine(
                metrics,
                routingExecutor,
                new SpaceTrackingEvents() {
                  @Override
                  public void gainInterestInSpace(String space) {
                    System.err.println("gain:" + space);
                  }

                  @Override
                  public void shareTargetsFor(String space, Set<String> targets) {
                    if ("space".equals(space) && targets.size() == 1) {
                      primed.countDown();
                    }
                  }

                  @Override
                  public void lostInterestInSpace(String space) {
                    System.err.println("lost:" + space);
                  }
                },
                50,
                25);
        InstanceClientFinder finder =
            new InstanceClientFinder(server.base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 1, engine, logger);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engine, finder, server.clientExecutor);
        Connection connection =
            new Connection(
                base,
                "origin",
                "me",
                "dev",
                "space",
                "key1", "{}",
                new MockSimpleEvents());
        connection.open();
        connection.close();
        finder.sync(new TreeSet<>(Collections.singleton("127.0.0.1:30001")));
        Assert.assertTrue(primed.await(15000, TimeUnit.MILLISECONDS));
      } finally {
        routingExecutor.shutdown();
      }
    }
  }
}
