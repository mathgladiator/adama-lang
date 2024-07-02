/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PreciseSchedulerBusTests {

  @Test
  public void huh() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("executor");
    PreciseSchedulerBus preciseScheduler = new PreciseSchedulerBus(100);
    Thread t = new Thread(preciseScheduler);
    t.start();
    CountDownLatch first = new CountDownLatch(1);
    Thread.sleep(1000);
    executor.execute(new NamedRunnable("dump-slf4j") {
      @Override
      public void execute() throws Exception {
        first.countDown();
      }
    });
    first.await(1000, TimeUnit.MILLISECONDS);
    System.out.println("asked-wait-ms,achieved-wait-ms");
    for (int latency = 0; latency < 40; latency++) {
      final int latencyEx = latency;
      final long started = System.currentTimeMillis();
      CountDownLatch latch = new CountDownLatch(1);
      preciseScheduler.schedule(executor, new NamedRunnable("precision") {
        @Override
        public void execute() throws Exception {
          System.out.println(latencyEx + "," + (System.currentTimeMillis() - started));
          latch.countDown();
        }
      }, latency);
      latch.await(10000, TimeUnit.MILLISECONDS);
    }
    t.interrupt();
  }

  @Test
  public void flow() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("executor");
    PreciseSchedulerBus bus = new PreciseSchedulerBus(100);
    Thread thread = new Thread(bus);
    thread.start();
    Thread.sleep(1000);
    for (int k = 0; k < 10; k++) {
      CountDownLatch firstIsSlow = new CountDownLatch(1);
      long startedWarm = System.currentTimeMillis();
      bus.schedule(executor, new NamedRunnable("fast") {
        @Override
        public void execute() throws Exception {
          System.out.println("Trend-line:" + (System.currentTimeMillis() - startedWarm));
          firstIsSlow.countDown();
        }
      }, 1);
      Assert.assertTrue(firstIsSlow.await(1000, TimeUnit.MILLISECONDS));
    }

    CountDownLatch latch = new CountDownLatch(2);
    long started = System.currentTimeMillis();
    bus.schedule(executor, new NamedRunnable("fast") {
      @Override
      public void execute() throws Exception {
        System.out.println("Time 1:" + (System.currentTimeMillis() - started));
        latch.countDown();
      }
    }, 1);
    bus.schedule(executor, new NamedRunnable("fast") {
      @Override
      public void execute() throws Exception {
        System.out.println("Time 2:" + (System.currentTimeMillis() - started));
        latch.countDown();
      }
    }, 5);
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    thread.interrupt();
    thread.join();
  }
}
