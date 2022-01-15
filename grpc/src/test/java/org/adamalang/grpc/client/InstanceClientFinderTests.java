/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.SimpleExecutorFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InstanceClientFinderTests {

  @Test
  public void bigMesh() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[10];
    SimpleExecutor routingExecutor = SimpleExecutor.create("routing");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TreeSet<String> targets = new TreeSet<>();
      for (int k = 0; k < servers.length; k++) {
        servers[k] =
            new TestBed(
                20001 + k,
                "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
      }
      for (int k = 0; k < servers.length; k++) {
        servers[k].startServer();
        targets.add("127.0.0.1:" + (20001 + k));
      }
      CountDownLatch primed = new CountDownLatch(1);
      RoutingEngine engine =
          new RoutingEngine(
              metrics,
              routingExecutor,
              new SpaceTrackingEvents() {
                @Override
                public void gainInterestInSpace(String space) {}

                @Override
                public void shareTargetsFor(String space, Set<String> targets) {
                  if ("space".equals(space) && servers.length == targets.size()) {
                    primed.countDown();
                  }
                }

                @Override
                public void lostInterestInSpace(String space) {}
              },
              50,
              25);
      InstanceClientFinder finder = new InstanceClientFinder(metrics, null, servers[0].identity, SimpleExecutorFactory.DEFAULT, 2, engine, logger);
      try {
        finder.sync(targets);
        Assert.assertTrue(primed.await(25000, TimeUnit.MILLISECONDS));
        CountDownLatch latchFound = new CountDownLatch(1);
        finder.find(
            "127.0.0.1:20005",
            new QueueAction<>(100, 200) {
              @Override
              protected void executeNow(InstanceClient item) {
                latchFound.countDown();
              }

              @Override
              protected void failure(int code) {}
            });
        Assert.assertTrue(latchFound.await(25000, TimeUnit.MILLISECONDS));
      } finally {
        finder.shutdown();
      }
    } finally {
      for (int k = 0; k < servers.length; k++) {
        if (servers[k] != null) {
          servers[k].close();
        }
      }
      routingExecutor.shutdown();
    }
  }
}
