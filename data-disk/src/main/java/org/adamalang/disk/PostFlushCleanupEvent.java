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
