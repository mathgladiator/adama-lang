package org.adamalang.bald.benchmarks;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Experiment7 {

  public static class PreciseScheduler implements Runnable {
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

  public static void main(String[] args) throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("executor");
    PreciseScheduler preciseScheduler = new PreciseScheduler(100);
    Thread t = new Thread(preciseScheduler);
    t.start();
    CountDownLatch first = new CountDownLatch(1);
    Thread.sleep(1000);
    executor.execute(new NamedRunnable("dump-slf4j") {
      @Override
      public void execute() throws Exception {
        first.countDown();
      }
    });
    first.await(1000, TimeUnit.MILLISECONDS);
    System.out.println("asked-wait-ms,achieved-wait-ms");
    for (int latency = 0; latency < 40; latency++) {
      final int latencyEx = latency;
      final long started = System.currentTimeMillis();
      CountDownLatch latch = new CountDownLatch(1);
      preciseScheduler.schedule(executor, new NamedRunnable("precision") {
        @Override
        public void execute() throws Exception {
          System.out.println(latencyEx + "," + (System.currentTimeMillis() - started));
          latch.countDown();
        }
      }, latency);
      latch.await(10000, TimeUnit.MILLISECONDS);
    }
    t.interrupt();
  }
}
