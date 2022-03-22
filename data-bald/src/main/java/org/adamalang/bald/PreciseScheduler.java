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
