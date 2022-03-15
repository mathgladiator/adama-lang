package org.adamalang.disk;

import java.io.File;

public class PostFlushCleanupEvent {
  private final File fileToDelete;
  private int value;

  public PostFlushCleanupEvent(File file, int value) {
    this.fileToDelete = file;
    this.value = value;
  }

  public void finished() {
    value--;
    if (value == 0) {
      System.err.println(fileToDelete.getAbsolutePath() + "DELETED");
      fileToDelete.delete();
    }
  }
}
