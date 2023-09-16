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

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleExecutorTests {
  @Test
  public void coverageNow() {
    SimpleExecutor.NOW.execute(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {

      }
    });
    SimpleExecutor.NOW.schedule(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {

      }
    }, 100).run();
    SimpleExecutor.NOW.scheduleNano(new NamedRunnable("nano") {
      @Override
      public void execute() throws Exception {

      }
    }, 100).run();
    SimpleExecutor.NOW.shutdown();
  }

  @Test
  public void create() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("base");
    CountDownLatch latchExec = new CountDownLatch(3);
    executor.execute(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    });
    Runnable cancel1 = executor.schedule(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    }, 10);
    Runnable cancel2 = executor.scheduleNano(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    }, 10);
    Assert.assertTrue(latchExec.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(executor.shutdown().await(1000, TimeUnit.MILLISECONDS));
    cancel1.run();
    cancel2.run();
  }
}
