/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.play.benchmarks.scenarios;

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
    }), new File(location, "STORE"), location, 1024 * 1024 * 1024, 16 * 1024 * 1024, 64 * 1024 * 1024);
    try {
      AtomicInteger counter = new AtomicInteger(0);
      Scenario.Driver driver = new Scenario.Driver() {
        @Override
        public void append(String key, int seq, byte[] value) throws Exception {
          dls.append(seq, value, () -> {});
          if (counter.incrementAndGet() % 2500 == 0) {
            // dls.flush(false);
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
