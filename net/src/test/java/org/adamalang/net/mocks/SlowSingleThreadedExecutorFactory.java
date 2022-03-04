/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
          System.err.println(name + "|RUN:" + ((NamedRunnable) runnable).name);
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
    if (command.name.startsWith("client-heartbeat")) {
      return null;
    }
    if (command.name.startsWith("instance-client-heartbeat")) {
      return null;
    }
    if (command.name.startsWith("expire-action")) {
      return null;
    }
    if (command.name.startsWith("finder-proxy-add")) {
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
  public void schedule(NamedRunnable command, long milliseconds) {
    add(command);
  }

  @Override
  public CountDownLatch shutdown() {
    return null;
  }
}
