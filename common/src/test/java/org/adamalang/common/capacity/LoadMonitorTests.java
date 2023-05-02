/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.jvm.MachineHeat;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadMonitorTests {
  @Test
  public void monitor() throws Exception {
    MachineHeat.install();
    SimpleExecutor executor = SimpleExecutor.create("m");
    AtomicBoolean b = new AtomicBoolean(true);
    try {
      LoadMonitor lm = new LoadMonitor(executor, b);
      CountDownLatch latch = new CountDownLatch(2);
      lm.cpu(new LoadEvent(-1, (b1) -> latch.countDown()));
      lm.memory(new LoadEvent(-1, (b2) -> latch.countDown()));
      Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    } finally {
      b.set(false);
      executor.shutdown();
    }
  }
}
