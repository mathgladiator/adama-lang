package org.adamalang.disk;

import org.adamalang.common.metrics.Inflight;

import java.io.File;

public class PostFlushCleanupEvent {
  private final Inflight inflight;
  private final File fileToDelete;
  private int value;

  public PostFlushCleanupEvent(Inflight inflight, File file, int value) {
    this.inflight = inflight;
    this.fileToDelete = file;
    this.value = value;
  }

  public void finished() {
    value--;
    if (value == 0) {
      System.err.println(fileToDelete.getAbsolutePath() + "DELETED");
      fileToDelete.delete();
      inflight.down();
    }
  }
}
