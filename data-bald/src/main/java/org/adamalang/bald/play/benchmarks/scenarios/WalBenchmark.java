package org.adamalang.bald.play.benchmarks.scenarios;

import org.adamalang.bald.data.WriteAheadLog;
import org.adamalang.bald.wal.Append;

import java.io.File;

public class WalBenchmark {
  public static void main(String[] args) throws Exception {
    Scenario[] scenarios = new Scenario[10];
    for (int k = 0; k < scenarios.length; k++) {
      scenarios[k] = new Scenario(2000, 50, new String[] {"0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"});
    }
    File walFile = File.createTempFile("datafile", "sample");
    WriteAheadLog log = new WriteAheadLog(walFile, 64 * 1024);
    try {
      Scenario.Driver driver = new Scenario.Driver() {
        @Override
        public void append(String key, int seq, byte[] value) throws Exception {
          log.write(new Append(key.hashCode(), 0, value));
        }

        @Override
        public void flush() throws Exception {
          log.flush();
        }
      };
      for (int k = 0; k < scenarios.length; k++) {
        scenarios[k].drive(driver);
      }
    } finally {
      walFile.delete();
    }
  }
}
