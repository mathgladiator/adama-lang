/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
