/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.sys.metering;

import org.adamalang.common.ExceptionRunnable;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.json.JsonStreamReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * writes billing information to disk for querying; this is designed to survive a process restart
 * using a disk log
 */
public class DiskMeteringBatchMaker {
  private final TimeSource time;
  private final SimpleExecutor executor;
  private final File root;
  private final long cutOffMilliseconds;
  private final File current;
  private FileOutputStream output;
  private long oldestTime;
  private final MeteringBatchReady ready;

  public DiskMeteringBatchMaker(TimeSource time, SimpleExecutor executor, File root, long cutOffMilliseconds, MeteringBatchReady ready) throws Exception {
    this.time = time;
    this.executor = executor;
    this.root = root;
    this.cutOffMilliseconds = cutOffMilliseconds;
    this.oldestTime = time.nowMilliseconds();
    this.current = new File(root, "CURRENT");
    this.ready = ready;
    if (current.exists()) {
      File transfer = new File(root, "TRANSFER");
      try (FileOutputStream transferStream = new FileOutputStream(transfer)) {
        try (FileReader inputReader = new FileReader(current)) {
          try (BufferedReader reader = new BufferedReader(inputReader)) {
            String ln;
            while ((ln = reader.readLine()) != null) {
              MeterReading meterReading = MeterReading.unpack(new JsonStreamReader(ln));
              if (meterReading != null) {
                if (meterReading.time < oldestTime) {
                  oldestTime = meterReading.time;
                }
                transferStream.write((meterReading.packup() + "\n").getBytes(StandardCharsets.UTF_8));
              }
            }
          }
        }
        transferStream.flush();
        transferStream.close();
        current.delete(); // accept the tiny window of data loss
        transfer.renameTo(current);
      }
    }
    this.output = new FileOutputStream(current, true);
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(new ExceptionRunnable() {
      @Override
      public void run() throws Exception {
        close();
      }
    })));
    ready.init(this);
    executor.schedule(new NamedRunnable("wait-for-warm-up") {
      @Override
      public void execute() throws Exception {
        for (File file : root.listFiles()) {
          if (file.getName().startsWith("SUMMARY-")) {
            ready.ready(file.getName().substring(8));
          }
        }
      }
    }, 30000);
  }

  public void close() throws Exception {
    output.flush();
    output.close();
  }

  public void flush(CountDownLatch done) {
    this.executor.execute(new NamedRunnable("billing-flush") {
      @Override
      public void execute() throws Exception {
        cut();
        done.countDown();
      }
    });
  }

  private void cut() throws Exception {
    String batchId = UUID.randomUUID() + "_" + time.nowMilliseconds();
    File cuttingBatch = new File(root, "CUT-" + batchId);
    try {
      output.flush();
      output.close();
      current.renameTo(cuttingBatch);
      oldestTime = time.nowMilliseconds();
      output = new FileOutputStream(current, true);
      MeterReducer reducer = new MeterReducer(time);
      try (FileReader reader = new FileReader(cuttingBatch)) {
        try (BufferedReader buffered = new BufferedReader(reader)) {
          String ln;
          while ((ln = buffered.readLine()) != null) {
            MeterReading meterReading = MeterReading.unpack(new JsonStreamReader(ln));
            if (meterReading != null) {
              reducer.next(meterReading);
            }
          }
        }
      }
      File inflightSummary = new File(root, "TEMP-SUMMARY-" + batchId);
      File finalSummary = new File(root, "SUMMARY-" + batchId);
      Files.writeString(inflightSummary.toPath(), reducer.toJson());
      inflightSummary.renameTo(finalSummary);
      ready.ready(batchId);
    } finally {
      // if we fail, then we simply delete the batch
      cuttingBatch.delete();
    }
  }

  public void write(MeterReading meterReading) {
    byte[] meterBytes = (meterReading.packup() + "\n").getBytes(StandardCharsets.UTF_8);
    this.executor.execute(new NamedRunnable("billing-add-sample") {
      @Override
      public void execute() throws Exception {
        output.write(meterBytes);
        // we don't flush for performance reasons, and we are willing to let records slide on a
        // problem
        long delta = time.nowMilliseconds() - oldestTime;
        if (delta > cutOffMilliseconds) {
          cut();
        }
      }
    });
  }

  public String getNextAvailableBatchId() {
    for (File file : root.listFiles()) {
      if (file.getName().startsWith("SUMMARY-")) {
        return file.getName().substring(8);
      }
    }
    return null;
  }

  public String getBatch(String batchId) throws Exception {
    return Files.readString(new File(root, "SUMMARY-" + batchId).toPath());
  }

  public void deleteBatch(String batchId) throws Exception {
    Files.delete(new File(root, "SUMMARY-" + batchId).toPath());
  }
}
