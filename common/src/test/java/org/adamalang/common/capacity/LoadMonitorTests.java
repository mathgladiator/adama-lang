/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
import org.adamalang.common.jvm.MachineHeat;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadMonitorTests {
  @Test
  public void monitor() throws Exception {
    MachineHeat.install();
    SimpleExecutor executor = SimpleExecutor.create("m");
    AtomicBoolean b = new AtomicBoolean(true);
    try {
      LoadMonitor lm = new LoadMonitor(executor, b);
      CountDownLatch latch = new CountDownLatch(2);
      lm.cpu(new LoadEvent("cpu", -1, (b1) -> latch.countDown()));
      lm.memory(new LoadEvent("mem", -1, (b2) -> latch.countDown()));
      Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    } finally {
      b.set(false);
      executor.shutdown();
    }
  }
}
