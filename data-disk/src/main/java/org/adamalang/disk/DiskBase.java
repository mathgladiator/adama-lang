package org.adamalang.disk;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.Key;

import java.io.File;
import java.util.HashMap;

public class DiskBase {
  public final SimpleExecutor executor;
  public final HashMap<Key, DocumentMemoryLog> memory;
  public final File rootDirectory;
  public final File walWorkingDirectory;
  public final int walCutOffBytes;
  public final int nanosecondsToFlush;
  public final DiskWriteAheadLog log = null;

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
      log = new DocumentMemoryLog();
      memory.put(key, log);
    }
    return log;
  }

  public File dataFileFor(Key key, String type) {
     return null;
  }
}
