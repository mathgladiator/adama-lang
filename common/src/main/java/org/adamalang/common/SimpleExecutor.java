/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

import java.util.concurrent.*;

/** wraps Java executor for time and simplifies for Adama */
public interface SimpleExecutor {
  /** a default instance for doing things NOW */
  SimpleExecutor NOW = new SimpleExecutor() {
    @Override
    public void execute(NamedRunnable command) {
      command.run();
    }

    @Override
    public Runnable schedule(NamedRunnable command, long milliseconds) {
      return () -> {
      };
    }

    @Override
    public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
      return () -> {
      };
    }

    @Override
    public CountDownLatch shutdown() {
      return new CountDownLatch(0);
    }
  };

  // TODO: INSTRUMENT THIS
  static SimpleExecutor create(String name) {
    ScheduledExecutorService realExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
    return new SimpleExecutor() {
      @Override
      public void execute(NamedRunnable command) {
        realExecutor.execute(command);
      }

      @Override
      public Runnable schedule(NamedRunnable command, long milliseconds) {
        ScheduledFuture<?> future = realExecutor.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
        return () -> future.cancel(false);
      }

      @Override
      public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
        ScheduledFuture<?> future = realExecutor.schedule(command, nanoseconds, TimeUnit.NANOSECONDS);
        return () -> future.cancel(false);
      }

      @Override
      public CountDownLatch shutdown() {
        CountDownLatch latch = new CountDownLatch(1);
        realExecutor.execute(() -> {
          latch.countDown();
          realExecutor.shutdown();
        });
        return latch;
      }
    };
  }

  /** execute the given command in the executor */
  void execute(NamedRunnable command);

  /** schedule the given command to run after some milliseconds within the executor */
  Runnable schedule(NamedRunnable command, long milliseconds);

  Runnable scheduleNano(NamedRunnable command, long nanoseconds);

  /** shutdown the executor */
  CountDownLatch shutdown();
}
