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
package org.adamalang.net.mocks;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.SimpleExecutorFactory;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SlowSingleThreadedExecutorFactory implements SimpleExecutorFactory, SimpleExecutor {
  private final String name;
  private final ArrayList<CountDownLatch> latches;
  private final ArrayList<Runnable> runnables;
  private boolean fast = false;

  public SlowSingleThreadedExecutorFactory(String name) {
    this.name = name;
    this.latches = new ArrayList<>();
    this.runnables = new ArrayList<>();
  }

  public synchronized void survey() {
    for (Runnable runnable : runnables) {
      System.err.println("QUEUE:" + name + "|" + runnable);
    }
  }

  public synchronized void goFast() {
    fast = true;
    for (Runnable runnable : runnables) {
      runnable.run();
    }
  }

  public synchronized Runnable latchAtUnderLock(int k) {
    CountDownLatch latch = new CountDownLatch(k);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      } catch (Exception ex) {
        Assert.fail();
      }
    };
  }

  private synchronized Runnable drainUnderLock(int expectedSize) {
    ArrayList<Runnable> copy = new ArrayList<>(runnables);
    if (expectedSize > 0) {
      Assert.assertEquals(expectedSize, copy.size());
    }
    runnables.clear();
    return () -> {
      for (Runnable runnable : copy) {
        if (runnable instanceof NamedRunnable) {
          System.err.println(name + "|RUN:" + ((NamedRunnable) runnable).__runnableName);
        }
        runnable.run();
      }
    };
  }

  public Runnable latchAtAndDrain(int at, int expectedSize) {
    Runnable a = latchAtUnderLock(at);
    return () -> {
      a.run();
      drainUnderLock(expectedSize).run();
    };
  }

  private void add(NamedRunnable command) {
    NamedRunnable toRun = addSync(command);
    if (toRun != null) {
      toRun.run();
    }
  }

  private synchronized NamedRunnable addSync(NamedRunnable command) {
    if (command.__runnableName.startsWith("client-heartbeat")) {
      return null;
    }
    if (command.__runnableName.startsWith("instance-client-heartbeat")) {
      return null;
    }
    if (command.__runnableName.startsWith("expire-action")) {
      return null;
    }
    if (command.__runnableName.startsWith("finder-proxy-add")) {
      System.err.println(name + "|NOW:" + command);
      return command;
    }
    System.err.println(name + "|ADD:" + command);
    if (fast) {
      return command;
    }
    runnables.add(command);
    Iterator<CountDownLatch> it = latches.iterator();
    while (it.hasNext()) {
      CountDownLatch latch = it.next();
      latch.countDown();
      if (latch.getCount() == 0) {
        it.remove();
      }
    }
    return null;
  }

  @Override
  public SimpleExecutor makeSingle(String name) {
    return this;
  }

  @Override
  public SimpleExecutor[] makeMany(String name, int nThreads) {
    SimpleExecutor[] children = new SimpleExecutor[nThreads];
    for (int k = 0; k < children.length; k++) {
      children[k] = this;
    }
    return children;
  }

  @Override
  public void execute(NamedRunnable command) {
    add(command);
  }

  @Override
  public Runnable schedule(NamedRunnable command, long milliseconds) {
    add(command);
    return () -> {};
  }

  @Override
  public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
    add(command);
    return () -> {};
  }

  @Override
  public CountDownLatch shutdown() {
    return null;
  }
}
