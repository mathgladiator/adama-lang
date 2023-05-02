/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RepeatingSignalTests {
  @Test
  public void repeat() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("m");
    AtomicBoolean b = new AtomicBoolean(true);
    try {
      CountDownLatch lTrue = new CountDownLatch(5);
      CountDownLatch lFalse = new CountDownLatch(5);
      RepeatingSignal signal = new RepeatingSignal(executor, b, 10, (x) -> {
        (x ? lTrue : lFalse).countDown();;
      });
      Assert.assertTrue(lFalse.await(1000, TimeUnit.MILLISECONDS));
      signal.accept(true);
      Assert.assertTrue(lTrue.await(1000, TimeUnit.MILLISECONDS));
    } finally {
      b.set(false);
      executor.shutdown();
    }
  }
}
