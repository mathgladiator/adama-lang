/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.canary;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CanaryMetricsRegister {
  public final AtomicInteger success_connects;
  public final AtomicInteger failure_connects;
  public final AtomicLong bandwidth;

  public CanaryMetricsRegister() {
    this.success_connects = new AtomicInteger(0);
    this.failure_connects = new AtomicInteger(0);
    this.bandwidth = new AtomicLong(0);
  }

  public void poll() throws InterruptedException {
    System.out.println("time,success_connects,failure_connects,bandwidth");
    while (true) {
      System.out.println(System.currentTimeMillis() + "," + success_connects.get() + "," + failure_connects.get() + "," + bandwidth.get());
      Thread.sleep(1000);
    }
  }
}
