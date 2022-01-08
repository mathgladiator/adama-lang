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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** wraps Java executor for time and simplifies for Adama */
public interface SimpleExecutor {
  /** a default instance for doing things NOW */
  SimpleExecutor NOW =
      new SimpleExecutor() {
        @Override
        public void execute(Runnable command) {
          command.run();
        }

        @Override
        public void schedule(Runnable command, long milliseconds) {
          // no-op
        }

        @Override
        public CountDownLatch shutdown() {
          return new CountDownLatch(0);
        }
      };

  static SimpleExecutor create(String name) {
    ScheduledExecutorService realExecutor =
        Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
    return new SimpleExecutor() {
      @Override
      public void execute(Runnable command) {
        realExecutor.execute(command);
      }

      @Override
      public void schedule(Runnable command, long milliseconds) {
        realExecutor.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
      }

      @Override
      public CountDownLatch shutdown() {
        CountDownLatch latch = new CountDownLatch(1);
        realExecutor.execute(
            () -> {
              latch.countDown();
              realExecutor.shutdown();
            });
        return latch;
      }
    };
  }

  /** execute the given command in the executor */
  void execute(Runnable command);

  /** schedule the given command to run after some milliseconds within the executor */
  void schedule(Runnable command, long milliseconds);

  /** shutdown the executor */
  CountDownLatch shutdown();
}
