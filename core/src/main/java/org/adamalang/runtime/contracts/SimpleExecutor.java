/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.contracts;

import java.util.concurrent.CountDownLatch;

/** wraps Java executor for time and simplifies for Adama */
public interface SimpleExecutor {
  /** a default instance for doing things NOW */
  public static final SimpleExecutor NOW =
      new SimpleExecutor() {
        @Override
        public void execute(Runnable command) {
          command.run();
        }

        @Override
        public void schedule(Key key, Runnable command, long milliseconds) {
          // no-op
        }

        @Override
        public CountDownLatch shutdown() {
          return new CountDownLatch(0);
        }
      };

  /** execute the given command in the executor */
  void execute(Runnable command);

  /** schedule the given command to run after some milliseconds within the executor */
  void schedule(Key key, Runnable command, long milliseconds);

  /** shutdown the executor */
  CountDownLatch shutdown();
}
