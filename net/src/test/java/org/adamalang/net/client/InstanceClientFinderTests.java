/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.net.client;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.mocks.MockRoutingTarget;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InstanceClientFinderTests {

  @Test
  public void bigMesh() throws Exception {
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[10];
    SimpleExecutor routingExecutor = SimpleExecutor.create("routing");
    ExceptionLogger logger = (t, c) -> {};
    try {
      TestClientConfig clientConfig = new TestClientConfig();
      TreeSet<String> targets = new TreeSet<>();
      for (int k = 0; k < servers.length; k++) {
        servers[k] =
            new TestBed(
                20001 + k,
                "@connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");
      }
      for (int k = 0; k < servers.length; k++) {
        servers[k].startServer();
        targets.add("127.0.0.1:" + (20001 + k));
      }
      MockRoutingTarget target = new MockRoutingTarget();
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 2, target, logger);
      try {
        finder.sync(targets);
        CountDownLatch latchFoundSame = new CountDownLatch(1);
        finder.findCapacity(targets, (x) -> {
          Assert.assertTrue(x == targets);
          latchFoundSame.countDown();
        }, targets.size() - 1);
        Assert.assertTrue(latchFoundSame.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch foundNewOne = new CountDownLatch(1);
        finder.findCapacity(new TreeSet<>(), (x) -> {
          foundNewOne.countDown();
        }, 1);
        Assert.assertTrue(foundNewOne.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch latchFound = new CountDownLatch(1);
        finder.find("127.0.0.1:20005", new Callback<InstanceClient>() {
          @Override
          public void success(InstanceClient value) {
            latchFound.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
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
