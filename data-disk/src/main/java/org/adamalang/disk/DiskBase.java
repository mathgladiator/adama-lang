/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.Key;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class DiskBase {
  public final SimpleExecutor executor;
  public final HashMap<Key, DocumentMemoryLog> memory;
  public final File dataDirectory;
  public final File walWorkingDirectory;
  public final int walCutOffBytes;
  public final int nanosecondsToFlush;
  public final DiskDataMetrics metrics;
  private final LinkedList<DocumentMemoryLog> currentScanQueue;

  public DiskBase(DiskDataMetrics metrics, SimpleExecutor executor, File root) throws Exception {
    this.executor = executor;
    this.metrics = metrics;
    this.memory = new HashMap<>();
    this.walWorkingDirectory = new File(root, "wal");
    this.dataDirectory = new File(root, "data");
    this.walCutOffBytes = 16 * 1024 * 1024;
    this.nanosecondsToFlush = 1000000;
    if (!(walWorkingDirectory.exists() && walWorkingDirectory.isDirectory()) && !walWorkingDirectory.mkdirs()) {
      throw new RuntimeException("Failed to detect/find/create wal working directory:" + walWorkingDirectory.getAbsolutePath());
    }
    if (!(dataDirectory.exists() && dataDirectory.isDirectory()) && !dataDirectory.mkdirs()) {
      throw new RuntimeException("Failed to detect/find/create root directory:" + dataDirectory.getAbsolutePath());
    }
    currentScanQueue = new LinkedList<>();

  }

  public void start() {
    executor.schedule(new Scanner(), 10);
  }

  public DocumentMemoryLog getOrCreate(Key key) {
    DocumentMemoryLog log = memory.get(key);
    if (log == null) {
      File spacePath = new File(dataDirectory, key.space);
      if (!spacePath.exists()) {
        spacePath.mkdirs();
      }
      log = new DocumentMemoryLog(key, spacePath);
      memory.put(key, log);
    }
    return log;
  }

  public void attachFile(File fileToDelete) {
    PostFlushCleanupEvent event = new PostFlushCleanupEvent(metrics.disk_data_open_wal_files, fileToDelete, memory.size());
    for (DocumentMemoryLog log : memory.values()) {
      log.attach(event);
    }
  }

  public void flushAllNow(boolean reset) throws IOException {
    for (DocumentMemoryLog log : memory.values()) {
      log.flush();
    }
    if (reset) {
      memory.clear();
    }
  }

  public void shutdown() {
    executor.shutdown();
  }

  private class Scanner extends NamedRunnable {
    public Scanner() {
      super("scanner-state-machine");
    }

    @Override
    public void execute() throws Exception {
      try {
        if (currentScanQueue.size() == 0) {
          currentScanQueue.addAll(memory.values());
          return;
        }

        long started = System.currentTimeMillis();
        try {
          while (System.currentTimeMillis() < started + 5) {
            for (int j = 0; j < 5; j++) {
              DocumentMemoryLog log = currentScanQueue.removeFirst();
              if (log.hasActivityToFlush()) {
                metrics.disk_data_flush_file.run();
                log.flush();
              } else if (log.age() >= 60000) {
                memory.remove(log.key);
                metrics.disk_data_unload.run();
                // TODO: schedule an archival if available
              }
            }
          }
        } catch (NoSuchElementException nsee) {

        }
      } catch (Exception ex) {
        ex.printStackTrace();
      } finally {
        executor.schedule(this, 5);
      }
    }
  }
}
