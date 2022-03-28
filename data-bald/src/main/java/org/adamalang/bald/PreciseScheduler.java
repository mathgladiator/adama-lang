/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;

public class PreciseScheduler implements Runnable {
  private final ArrayList<Runnable>[] schedule;
  private int at;

  public PreciseScheduler(int max) {
    this.schedule = new ArrayList[max];
    this.at = 0;
    for (int k = 0; k < max; k++) {
      schedule[k] = new ArrayList<>();
    }
  }

  @Override
  public void run() {
    while (true) {
      ArrayList<Runnable> local = null;
      synchronized (schedule) {
        if (schedule[at].size() > 0) {
          local = new ArrayList<>(schedule[at]);
          schedule[at].clear();
        }
        at++;
        at %= schedule.length;
      }
      if (local != null) {
        for (Runnable task : local) {
          task.run();
        }
      }
      try {
        Thread.sleep(1);
      } catch (InterruptedException ie) {
        return;
      }
    }
  }

  public void schedule(SimpleExecutor executor, NamedRunnable runnable, int future) {
    synchronized (schedule) {
      schedule[(at + future / 2) % schedule.length].add(() -> {
        executor.execute(runnable);
      });
    }
  }
}
