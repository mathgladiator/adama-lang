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

import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemActionTests {

  private static final ItemActionMonitor.ItemActionMonitorInstance INSTANCE = new NoOpMetricsFactory().makeItemActionMonitor("x").start();

  @Test
  public void normal() {
    AtomicInteger x = new AtomicInteger(0);
    ItemAction<String> action = new ItemAction<>(100, 200, INSTANCE) {
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
    ItemAction<String> action = new ItemAction<>(100, 200, INSTANCE) {
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
    ItemAction<String> action = new ItemAction<String>(100, 1000, INSTANCE) {
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
