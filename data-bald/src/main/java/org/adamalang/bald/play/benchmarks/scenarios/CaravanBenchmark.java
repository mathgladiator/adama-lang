package org.adamalang.bald.play.benchmarks.scenarios;

import org.adamalang.bald.data.DataFile;
import org.adamalang.bald.data.WriteAheadLog;
import org.adamalang.bald.organization.Heap;
import org.adamalang.bald.organization.Region;
import org.adamalang.bald.wal.Append;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DurableListStoreMetrics;
import org.adamalang.common.metrics.NoOpMetricsFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class CaravanBenchmark {
  public static void main(String[] args) throws Exception {
    Scenario[] scenarios = new Scenario[10];
    for (int k = 0; k < scenarios.length; k++) {
      scenarios[k] = new Scenario(2000, 50, new String[] {"0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"});
    }
    File location = File.createTempFile("datafile", "sample");
    location.delete();
    location.mkdirs();

    DurableListStore dls = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory() {
      @Override
      public Runnable counter(String name) {
        return () -> {
          // System.err.println("EXEC:" + name);
        };
      }
    }), new File(location, "STORE"), location, 1024 * 1024 * 1024, 8 * 1024 * 1024, 16 * 1024 * 1024);
    try {
      AtomicInteger counter = new AtomicInteger(0);
      Scenario.Driver driver = new Scenario.Driver() {
        @Override
        public void append(String key, int seq, byte[] value) throws Exception {
          dls.append(seq, value, () -> {});
          if (counter.incrementAndGet() % 2500 == 0) {
            dls.flush(false);
          }
        }

        @Override
        public void flush() throws Exception {
          dls.flush(true);
        }
      };
      for (int k = 0; k < scenarios.length; k++) {
        scenarios[k].drive(driver);
      }
    } finally {
      for (File file : location.listFiles()) {
        file.delete();
      }
      location.delete();
    }
  }
}
