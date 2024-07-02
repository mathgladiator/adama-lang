/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    } finally {
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
    } finally {
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
    } finally {
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
    } finally {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  public class MyItemAction extends ItemAction<String> {
    private final AtomicInteger sum = new AtomicInteger(0);
    private final CountDownLatch done = new CountDownLatch(1);

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
}
