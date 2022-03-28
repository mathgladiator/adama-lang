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

import org.rocksdb.FlushOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class RocksDBBenchmark {

  public static void main(String[] args) throws Exception {
    RocksDB.loadLibrary();
    Options options = new Options();
    options.setCreateIfMissing(true);

    Scenario[] scenarios = new Scenario[10];
    for (int k = 0; k < scenarios.length; k++) {
      scenarios[k] = new Scenario(2000, 50, new String[] {"0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"});

    }
    File dbFile = File.createTempFile("rocksdb", "sample");
    dbFile.delete();
    dbFile.mkdirs();
    try {
      RocksDB db = RocksDB.open(options, dbFile.getAbsolutePath());
      Scenario.Driver driver = new Scenario.Driver() {
        @Override
        public void append(String key, int seq, byte[] value) throws Exception {
          db.put((key + "/" + seq).getBytes(StandardCharsets.UTF_8), value);
        }

        @Override
        public void flush() throws Exception {
          FlushOptions flushOptions = new FlushOptions();
          flushOptions.setWaitForFlush(true);
          db.flush(flushOptions);
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
