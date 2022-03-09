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

import org.adamalang.disk.files.ForwardFileStreamEvents;
import org.adamalang.disk.files.SnapshotFileStreamEvents;
import org.adamalang.disk.files.SnapshotHeader;
import org.adamalang.disk.wal.WriteAheadMessage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class DocumentMemoryLog {
  private WriteAheadMessage.Snapshot snapshot;
  private boolean alive;
  public ArrayList<WriteAheadMessage> forward;
  public Stack<WriteAheadMessage> undo;
  private boolean reset;

  public DocumentMemoryLog() {
    this.alive = true;
    this.forward = new ArrayList<>();
    this.reset = false;
    this.undo = new Stack<>();
  }

  public void initialize(WriteAheadMessage.Initialize data) {
    if (!alive) {
      reset = true;
      alive = true;
    }
    forward.add(data);
  }

  public void patch(WriteAheadMessage.Patch patch) {
    forward.add(patch);
    undo.push(patch);
  }

  public void delete() {
    alive = false;
    forward.clear();
    undo.clear();
  }

  public void snapshot(WriteAheadMessage.Snapshot snapshot) {
    this.snapshot = snapshot;
    forward.clear();
  }

  public boolean deleted() {
    return !alive;
  }

  public boolean hasSnapshot() {
    return this.snapshot != null;
  }

  ArrayList<File> listForwardsOrdered() {
    // TODO: list the root, pattern match files and organize
    return null;
  }

  public void flush(File root) throws IOException {
    boolean active = false;
    if (snapshot != null) {
      byte[] document = snapshot.document.getBytes(StandardCharsets.UTF_8);
      SnapshotHeader header = new SnapshotHeader(snapshot.seq, snapshot.history, document.length, active);
      FileOutputStream output = new FileOutputStream(new File(root, "SNAPSHOT.temp"));
      SnapshotFileStreamEvents writer = SnapshotFileStreamEvents.writerFor(new DataOutputStream(output));
      writer.onHeader(header);
      ArrayList<File> filesToScan = listForwardsOrdered();
      for (int k = filesToScan.size() - 1; k >= 0; k--) {

      }
    } else {
      // TODO: write the forward to disk as FORWARD-N-COUNT.temp --> FORWARD-N-COUNT
    }
  }
}
