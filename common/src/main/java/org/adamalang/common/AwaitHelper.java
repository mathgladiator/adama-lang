package org.adamalang.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AwaitHelper {

  public static boolean block(CountDownLatch latch, int ms) {
    try {
      return latch.await(ms, TimeUnit.MILLISECONDS);
    } catch (InterruptedException ie) {
      return false;
    }
  }
}
