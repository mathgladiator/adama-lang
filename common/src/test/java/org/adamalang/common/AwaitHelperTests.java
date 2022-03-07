package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class AwaitHelperTests {
  @Test
  public void coverage() {
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch blocked = new CountDownLatch(1);
    CountDownLatch latch = new CountDownLatch(1);
    Thread t = new Thread(() -> {
      started.countDown();
      AwaitHelper.block(blocked, 500000);
      latch.countDown();
    });
    t.start();
    Assert.assertTrue(AwaitHelper.block(started, 1000));
    t.interrupt();
    Assert.assertTrue(AwaitHelper.block(latch, 1000));
  }
}
