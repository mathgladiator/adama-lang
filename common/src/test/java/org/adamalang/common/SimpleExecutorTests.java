/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleExecutorTests {
  @Test
  public void coverageNow() {
    SimpleExecutor.NOW.execute(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {

      }
    });
    SimpleExecutor.NOW.schedule(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {

      }
    }, 100);
    SimpleExecutor.NOW.shutdown();
  }

  @Test
  public void create() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("base");
    CountDownLatch latchExec = new CountDownLatch(2);
    executor.execute(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    });
    executor.schedule(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    }, 10);
    Assert.assertTrue(latchExec.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(executor.shutdown().await(1000, TimeUnit.MILLISECONDS));
  }
}
