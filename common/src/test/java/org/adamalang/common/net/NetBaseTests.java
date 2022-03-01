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
