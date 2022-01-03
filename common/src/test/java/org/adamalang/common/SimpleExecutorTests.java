/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleExecutorTests {
  @Test
  public void coverageNow() {
    SimpleExecutor.NOW.execute(() -> {});
    SimpleExecutor.NOW.schedule(() -> {}, 100);
    SimpleExecutor.NOW.shutdown();
  }

  @Test
  public void create() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("base");
    CountDownLatch latchExec = new CountDownLatch(2);
    executor.execute(latchExec::countDown);
    executor.schedule(latchExec::countDown, 10);
    Assert.assertTrue(latchExec.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(executor.shutdown().await(1000, TimeUnit.MILLISECONDS));
  }
}
