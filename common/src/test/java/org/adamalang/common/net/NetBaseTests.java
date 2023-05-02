/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.net;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class NetBaseTests {
  @Test
  public void interrupt() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread t = new Thread(() -> {
      started.countDown();
      NetBase.standardBlockerWait(latch);
      finished.countDown();
    });
    t.start();
    started.await();
    t.interrupt();
    finished.await();
  }
}
