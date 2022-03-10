/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary.agents.caravan;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.disk.DiskBase;
import org.adamalang.disk.DiskDataMetrics;
import org.adamalang.disk.DiskDataService;
import org.adamalang.disk.WriteAheadLog;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CaravanBenchmark {

  private static final RemoteDocumentUpdate INIT = new RemoteDocumentUpdate(1, 1, NtClient.NO_ONE, "REQUEST", "{\"x\":0}", "{}", false, 0, 100, UpdateType.AddUserData);

  public static void go() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("executor");
    DiskBase base = new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), executor, new File("caravan_benchmark"));
    WriteAheadLog log = new WriteAheadLog(base, 8196, 50000, 32 * 1024 * 1024);
    DiskDataService service = new DiskDataService(base, log);
    AtomicInteger queues = new AtomicInteger(0);
    AtomicInteger writes = new AtomicInteger(0);
    AtomicInteger failures = new AtomicInteger(0);
    final int N = 1000000;
    HashMap<Integer, Integer> failure_reasons = new HashMap<>();
    CountDownLatch done = new CountDownLatch(N + 1);
    Callback<Void> inc = new Callback<Void>() {
      @Override
      public void success(Void value) {
        writes.incrementAndGet();
        done.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        int code = ex.code;
        synchronized (failure_reasons) {
          Integer prior = failure_reasons.get(code);
          if (prior == null) {
            failure_reasons.put(code, 1);
          } else {
            failure_reasons.put(code, prior + 1);
          }
        }
        failures.incrementAndGet();
        done.countDown();
      }
    };

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String blah = "123456789";
          for (int k = 0; k < 2; k++) {
            blah = blah + blah + blah + blah;
          }
          Key key = new Key("space", "key");
          CountDownLatch init = new CountDownLatch(1);
          service.initialize(key, INIT, wrap(inc, init));
          init.await(100, TimeUnit.MILLISECONDS);
          for (int k = 0; k < N; k++) {
            CountDownLatch latch = new CountDownLatch(1);
            service.patch(key, new RemoteDocumentUpdate[]{UPDATE(k + 2, blah)}, wrap(inc, latch));
            latch.await(100, TimeUnit.MILLISECONDS);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }).start();

    Runnable snap = () -> {
      int snapshotWrites = writes.get();
      writes.addAndGet(-snapshotWrites);

      int snapshotQueues = queues.get();
      queues.addAndGet(-snapshotQueues);

      int snapshotFailures = failures.get();
      failures.addAndGet(-snapshotFailures);

      StringBuilder sb = new StringBuilder();
      sb.append(snapshotQueues + "," + snapshotWrites + "," + snapshotFailures).append("|");
      boolean append = false;
      for (Map.Entry<Integer, Integer> entry : failure_reasons.entrySet()) {
        if (append) {
          sb.append("|");
        }
        append = true;
        sb.append(entry.getKey() + "=" + entry.getValue());
      }
      System.err.println(sb);
    };

    while (!done.await(1000, TimeUnit.MILLISECONDS)) {
      snap.run();
    }
    snap.run();

  }

  public static Callback<Void> wrap(Callback<Void> cb, CountDownLatch latch) {
    return new Callback<Void>() {
      @Override
      public void success(Void value) {
        cb.success(null);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        cb.failure(ex);
        latch.countDown();
      }
    };
  }

  private static RemoteDocumentUpdate UPDATE(int n, String blah) {
    return new RemoteDocumentUpdate(n, n, null, "REQUEST", "{\"x\":" + n + ",\"blah\":\"" + "\"}", "{\"x\":" + (n - 1) + ",\"blah\":\"-" + blah + "\"}", false, 0, 100, UpdateType.AddUserData);
  }
}
