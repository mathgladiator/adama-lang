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
package org.adamalang.common.net;

import org.adamalang.common.AwaitHelper;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class NetBaseTests {
  @Test
  public void interrupt() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread t = new Thread(() -> {
      started.countDown();
      AwaitHelper.block(latch, 10000);
      finished.countDown();
    });
    t.start();
    started.await();
    t.interrupt();
    finished.await();
  }
}
