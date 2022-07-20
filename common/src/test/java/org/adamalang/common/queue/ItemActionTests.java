/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.queue;

import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemActionTests {

  private static ItemActionMonitor.ItemActionMonitorInstance INSTANCE = new NoOpMetricsFactory().makeItemActionMonitor("x").start();
  @Test
  public void normal() {
    AtomicInteger x = new AtomicInteger(0);
    ItemAction<String> action =
        new ItemAction<>(100, 200, INSTANCE) {
          @Override
          protected void executeNow(String item) {
            x.incrementAndGet();
          }

          @Override
          protected void failure(int code) {
            x.addAndGet(code);
          }
        };
    Assert.assertTrue(action.isAlive());
    action.execute("z");
    Assert.assertFalse(action.isAlive());
    Assert.assertEquals(1, x.get());
  }

  @Test
  public void timeout() {
    AtomicInteger x = new AtomicInteger(0);
    ItemAction<String> action =
        new ItemAction<>(100, 200, INSTANCE) {
          @Override
          protected void executeNow(String item) {
            x.incrementAndGet();
          }

          @Override
          protected void failure(int code) {
            x.addAndGet(code);
          }
        };
    Assert.assertTrue(action.isAlive());
    action.killDueToTimeout();
    Assert.assertFalse(action.isAlive());
    action.execute("z");
    action.execute("z");
    action.execute("z");
    Assert.assertFalse(action.isAlive());
    Assert.assertEquals(100, x.get());
  }

  @Test
  public void rejected() {
    AtomicInteger x = new AtomicInteger(0);
    ItemAction<String> action =
        new ItemAction<String>(100, 1000, INSTANCE) {
          @Override
          protected void executeNow(String item) {
            x.incrementAndGet();
          }

          @Override
          protected void failure(int code) {
            x.addAndGet(code);
          }
        };
    Assert.assertTrue(action.isAlive());
    action.killDueToReject();
    Assert.assertFalse(action.isAlive());
    action.execute("z");
    action.execute("z");
    action.execute("z");
    Assert.assertEquals(1000, x.get());
  }
}
