/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.files;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** since a snapshot file may be large, we stream it via events */
public interface SnapshotFileStreamEvents {
  /** stream a snapshot file from a DataInputStream */
  static void read(DataInputStream data, SnapshotFileStreamEvents events) throws IOException {
    SnapshotHeader header = SnapshotHeader.read(data);
    if (events.onHeader(header)) {
      byte[] document = new byte[header.documentSize];
      data.readFully(document);
      if (events.onDocument(document)) {
        while (data.readBoolean()) {
          int seq = data.readInt();
          byte[] undo = new byte[data.readInt()];
          data.readFully(undo);
          if (!events.onUndo(seq, new String(undo, StandardCharsets.UTF_8))) {
            events.onFinished(!data.readBoolean());
            return;
          }
        }
        events.onFinished(true);
      } else {
        events.onFinished(false);
      }
    } else {
      events.onFinished(false);
    }
  }

  /** header; return true if you want to continue reading */
  boolean onHeader(SnapshotHeader header) throws IOException;

  /** snapshot of the document (after header); return true if you want to continue reading */
  boolean onDocument(byte[] document) throws IOException;

  /** undo (after documents and undo); return true if you want to continue reading */
  boolean onUndo(int seq, String undo) throws IOException;

  /** finished */
  void onFinished(boolean complete) throws IOException;

  /** create a writer to produce a snapshot file */
  static SnapshotFileStreamEvents writerFor(DataOutputStream output) {
    return new SnapshotFileStreamEvents() {
      @Override
      public boolean onHeader(SnapshotHeader header) throws IOException {
        header.write(output);
        return true;
      }

      @Override
      public boolean onDocument(byte[] document) throws IOException {
        output.write(document);
        return true;
      }

      @Override
      public boolean onUndo(int seq, String undo) throws IOException {
        output.writeBoolean(true);
        output.writeInt(seq);
        byte[] bytes = undo.getBytes(StandardCharsets.UTF_8);
        output.writeInt(bytes.length);
        output.write(bytes);
        return true;
      }

      @Override
      public void onFinished(boolean complete) throws IOException {
        output.writeBoolean(false);
        output.flush();
      }
    };
  }
}
