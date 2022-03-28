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

import org.adamalang.bald.data.DataFile;
import org.adamalang.bald.data.WriteAheadLog;
import org.adamalang.bald.organization.Heap;
import org.adamalang.bald.organization.Region;
import org.adamalang.bald.wal.Append;

import java.io.File;

public class BothWALAndDataFileBenchmark {
  public static void main(String[] args) throws Exception {
    Scenario[] scenarios = new Scenario[10];
    for (int k = 0; k < scenarios.length; k++) {
      scenarios[k] = new Scenario(2000, 50, new String[] {"0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"});
    }
    File walFile = File.createTempFile("datafile", "sample");
    WriteAheadLog log = new WriteAheadLog(walFile, 64 * 1024 * 1024);
    File dbFile = File.createTempFile("datafile", "sample");
    DataFile df = new DataFile(dbFile, 1024 * 1024 * 1024);
    Heap heap = new Heap(1024 * 1024 * 1024);

    try {
      Scenario.Driver driver = new Scenario.Driver() {
        @Override
        public void append(String key, int seq, byte[] value) throws Exception {
          Region region = heap.ask(value.length);
          log.write(new Append(key.hashCode(), region.position, value));
          df.write(region, value);
        }

        @Override
        public void flush() throws Exception {
          log.flush();
          df.flush();
        }
      };
      for (int k = 0; k < scenarios.length; k++) {
        scenarios[k].drive(driver);
      }
    } finally {
      dbFile.delete();
      walFile.delete();
    }
  }
}
