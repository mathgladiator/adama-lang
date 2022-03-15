/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.mocks;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.concurrent.CountDownLatch;

public abstract class FilteredSimpleExecutor implements SimpleExecutor {
  private final SimpleExecutor real;

  public FilteredSimpleExecutor(SimpleExecutor real) {
    this.real = real;
  }

  public abstract boolean test(NamedRunnable runnable);

  @Override
  public void execute(NamedRunnable command) {
    if (test(command)) {
      real.execute(command);
    }
  }

  @Override
  public Runnable schedule(NamedRunnable command, long milliseconds) {
    if (test(command)) {
      return real.schedule(command, milliseconds);
    }
    return () -> {};
  }

  @Override
  public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
    if (test(command)) {
      return real.scheduleNano(command, nanoseconds);
    }
    return () -> {};
  }

  @Override
  public CountDownLatch shutdown() {
    return real.shutdown();
  }
}
