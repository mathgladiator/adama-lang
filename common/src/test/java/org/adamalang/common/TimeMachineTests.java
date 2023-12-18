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
package org.adamalang.common;

import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class TimeMachineTests {
  @Test
  public void flux_capacitor() throws Exception {
    MockTime mock = new MockTime();
    SimpleExecutor executor = SimpleExecutor.create("time");
    try {
      AtomicInteger at = new AtomicInteger(0);
      TimeMachine machine = new TimeMachine(mock, executor, () -> {
        at.incrementAndGet();
      });
      int attempts = 0;
      machine.add(2678400000L, 4);
      while (at.get() < 40 && attempts <= 500) {
        attempts++;
        Thread.sleep(100);
        System.err.println("@" + at.get() + "-->" + machine.nowMilliseconds());
      }
      Assert.assertEquals(40, at.get());
      Assert.assertEquals(2678400000L, machine.nowMilliseconds());
      Assert.assertTrue(attempts < 500);
    } finally {
      executor.shutdown();
    }
  }
}
