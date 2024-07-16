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
package org.adamalang.runtime.remote.replication;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SequencedTestExecutor implements SimpleExecutor {

  private final ArrayList<NamedRunnable> runnables;

  public void next() {
    Assert.assertTrue(runnables.size() > 0);
    runnables.remove(0).run();
  }

  public void assertEmpty() {
    Assert.assertEquals(0, runnables.size());
  }

  public SequencedTestExecutor() {
    this.runnables = new ArrayList<>();
  }

  @Override
  public void execute(NamedRunnable command) {
    this.runnables.add(command);
  }

  @Override
  public Runnable schedule(NamedRunnable command, long milliseconds) {
    this.runnables.add(command);
    return command;
  }

  @Override
  public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
    this.runnables.add(command);
    return command;
  }

  @Override
  public CountDownLatch shutdown() {
    return new CountDownLatch(0);
  }
}
