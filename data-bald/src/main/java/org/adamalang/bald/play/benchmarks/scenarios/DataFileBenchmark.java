package org.adamalang.bald.play.benchmarks.scenarios;

import org.adamalang.bald.data.DataFile;
import org.adamalang.bald.organization.Heap;
import org.adamalang.bald.organization.Region;

import java.io.File;

public class DataFileBenchmark {
  public static void main(String[] args) throws Exception {
    Scenario[] scenarios = new Scenario[10];
    for (int k = 0; k < scenarios.length; k++) {
      scenarios[k] = new Scenario(2000, 50, new String[] {"0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"});
    }
    File dbFile = File.createTempFile("datafile", "sample");
    DataFile df = new DataFile(dbFile, 1024 * 1024 * 1024);
    Heap heap = new Heap(1024 * 1024 * 1024);
    try {
      Scenario.Driver driver = new Scenario.Driver() {
        @Override
        public void append(String key, int seq, byte[] value) throws Exception {
          Region region = heap.ask(value.length);
          df.write(region, value);
        }

        @Override
        public void flush() throws Exception {
          df.flush();
        }
      };
      for (int k = 0; k < scenarios.length; k++) {
        scenarios[k].drive(driver);
      }
    } finally {
      dbFile.delete();
    }
  }
}
