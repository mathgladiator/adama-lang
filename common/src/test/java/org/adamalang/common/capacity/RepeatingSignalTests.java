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
package org.adamalang.common.capacity;

import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RepeatingSignalTests {
  @Test
  public void repeat() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("m");
    AtomicBoolean b = new AtomicBoolean(true);
    try {
      CountDownLatch lTrue = new CountDownLatch(5);
      CountDownLatch lFalse = new CountDownLatch(5);
      RepeatingSignal signal = new RepeatingSignal(executor, b, 10, (x) -> {
        (x ? lTrue : lFalse).countDown();
      });
      Assert.assertTrue(lFalse.await(1000, TimeUnit.MILLISECONDS));
      signal.accept(true);
      Assert.assertTrue(lTrue.await(1000, TimeUnit.MILLISECONDS));
    } finally {
      b.set(false);
      executor.shutdown();
    }
  }
}
