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
import java.util.*;

public class DiskBase {
  public final SimpleExecutor executor;
  public final HashMap<Key, DocumentMemoryLog> memory;
  public final File rootDirectory;
  public final File walWorkingDirectory;
  public final int walCutOffBytes;
  public final int nanosecondsToFlush;
  public final WriteAheadLog log = null;
  public final DiskDataMetrics metrics = null;

  public DiskBase(SimpleExecutor executor, File walWorkingDirectory, File root) throws Exception {
    this.executor = executor;
    this.memory = new HashMap<>();
    this.walWorkingDirectory = walWorkingDirectory;
    this.rootDirectory = root;
    this.walCutOffBytes = 16 * 1024 * 1024;
    this.nanosecondsToFlush = 1000000;
    if (!(walWorkingDirectory.exists() && walWorkingDirectory.isDirectory()) && !walWorkingDirectory.mkdir()) {
      throw new RuntimeException("Failed to detect/find/create root directory:" + root.getAbsolutePath());
    }
    if (!(rootDirectory.exists() && rootDirectory.isDirectory()) && !rootDirectory.mkdir()) {
      throw new RuntimeException("Failed to detect/find/create root directory:" + root.getAbsolutePath());
    }
  }

  public DocumentMemoryLog getOrCreate(Key key) {
    DocumentMemoryLog log = memory.get(key);
    if (log == null) {
      File spacePath = new File(rootDirectory, key.space);
      if (spacePath.exists()) {
        spacePath.mkdirs();
      }
      log = new DocumentMemoryLog(spacePath, key.key);
      memory.put(key, log);
    }
    return log;
  }

  public void remove(Key key) {
    memory.remove(key);
  }

  public void flush(File fileToDelete) {
    LinkedList<DocumentMemoryLog> logs = new LinkedList<>(memory.values());
    executor.scheduleNano(new NamedRunnable("flushing-document") {
      @Override
      public void execute() throws Exception {
        try {
          logs.removeFirst().flush();
          executor.schedule(this, 25000);
        } catch (NoSuchElementException nss) {
          fileToDelete.delete();
        }
      }
    }, 5000);
  }
}
