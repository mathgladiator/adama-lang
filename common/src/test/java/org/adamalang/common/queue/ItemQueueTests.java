/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.queue;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemQueueTests {

  public class MyItemAction extends ItemAction<String> {
    private AtomicInteger sum = new AtomicInteger(0);
    private CountDownLatch done = new CountDownLatch(1);

    public MyItemAction() {
      super(500, 1000, new NoOpMetricsFactory().makeItemActionMonitor("x").start());
    }

    @Override
    protected void executeNow(String item) {
      sum.addAndGet(1 + item.length());
      done.countDown();
    }

    @Override
    protected void failure(int code) {
      sum.addAndGet(100 + code);
      done.countDown();
    }

    public void awaitDone() throws Exception {
      Assert.assertTrue(done.await(5000, TimeUnit.MILLISECONDS));
    }

    public void assertSum(int x) {
      Assert.assertEquals(x, sum.get());
    }
  }

  @Test
  public void full() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("items");
    try {
      ItemQueue<String> queue = new ItemQueue<>(executor, 10, 5000);
      MyItemAction[] items = new MyItemAction[20];
      for (int k = 0; k < items.length; k++) {
        items[k] = new MyItemAction();
        queue.add(items[k]);
      }
      queue.ready("xyz");
      for (int k = 0; k < items.length; k++) {
        items[k].awaitDone();
      }
      for (int k = 0; k < 10; k++) {
        items[k].assertSum(4);
        items[k + 10].assertSum(1100);
      }
    } finally{
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void nuke() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("items");
    try {
      ItemQueue<String> queue = new ItemQueue<>(executor, 100, 5000);
      MyItemAction[] items = new MyItemAction[20];
      for (int k = 0; k < items.length; k++) {
        items[k] = new MyItemAction();
        queue.add(items[k]);
      }
      queue.unready();
      queue.nuke();
      for (int k = 0; k < items.length; k++) {
        items[k].awaitDone();
      }
      for (int k = 0; k < items.length; k++) {
        items[k].assertSum(1100);
      }
    } finally{
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void timeout() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("items");
    try {
      ItemQueue<String> queue = new ItemQueue<>(executor, 100, 25);
      MyItemAction[] items = new MyItemAction[20];
      for (int k = 0; k < items.length; k++) {
        items[k] = new MyItemAction();
        queue.add(items[k]);
      }
      for (int k = 0; k < items.length; k++) {
        items[k].awaitDone();
      }
      for (int k = 0; k < items.length; k++) {
        items[k].assertSum(600);
      }
    } finally{
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void ready() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("items");
    try {
      ItemQueue<String> queue = new ItemQueue<>(executor, 100, 25);
      MyItemAction[] items = new MyItemAction[20];
      queue.ready("x");
      for (int k = 0; k < items.length; k++) {
        items[k] = new MyItemAction();
        queue.add(items[k]);
      }
      for (int k = 0; k < items.length; k++) {
        items[k].awaitDone();
      }
    } finally{
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }
}
