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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleTimeoutTests {
  @Test
  public void make() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("test");
    try {
      CountDownLatch doesnt = new CountDownLatch(1);
      CountDownLatch fires = new CountDownLatch(1);
      SimpleTimeout.make(executor, 10, () -> {
        fires.countDown();
      });
      SimpleTimeout.make(executor, 50, () -> {
        doesnt.countDown();
      }).run();
      Assert.assertTrue(fires.await(1000, TimeUnit.MILLISECONDS));
      Assert.assertFalse(doesnt.await(100, TimeUnit.MILLISECONDS));
    } finally {
      executor.shutdown();
    }
  }

  @Test
  public void wrap() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("test");
    try {
      BiFunction<Runnable, Long, Callback<String>> cons = (action, ms) -> {
        Callback<String> toWrap = new Callback<String>() {
          @Override
          public void success(String value) {}

          @Override
          public void failure(ErrorCodeException ex) {}
        };
        return SimpleTimeout.WRAP(SimpleTimeout.make(executor, ms, action), toWrap);
      };
      {
        CountDownLatch latch = new CountDownLatch(1);
        cons.apply(() -> latch.countDown(), 5L).success("");
        Assert.assertFalse(latch.await(50, TimeUnit.MILLISECONDS));
      }
      {
        CountDownLatch latch = new CountDownLatch(1);
        cons.apply(() -> latch.countDown(), 5L).failure(new ErrorCodeException(0));
        Assert.assertFalse(latch.await(50, TimeUnit.MILLISECONDS));
      }
      {
        CountDownLatch latch = new CountDownLatch(1);
        cons.apply(() -> latch.countDown(), 50L);// dangle
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      }
    } finally {
      executor.shutdown();
    }
  }
}
