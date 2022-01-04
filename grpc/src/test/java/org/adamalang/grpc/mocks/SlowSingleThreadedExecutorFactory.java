/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.mocks;

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
    Assert.assertEquals(expectedSize, copy.size());
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

  private synchronized void add(Runnable command) {
    if (command instanceof NamedRunnable) {
      if (((NamedRunnable) command).name.startsWith("client-heartbeat")) {
        return;
      }
      if (((NamedRunnable) command).name.startsWith("instance-client-heartbeat")) {
        return;
      }
      System.err.println(name + "|ADD:" + ((NamedRunnable) command).name);
    } else {
      System.err.println(name + "|ADD:" + command);
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
  public void execute(Runnable command) {
    add(command);
  }

  @Override
  public void schedule(Runnable command, long milliseconds) {
    add(command);
  }

  @Override
  public CountDownLatch shutdown() {
    return null;
  }
}
