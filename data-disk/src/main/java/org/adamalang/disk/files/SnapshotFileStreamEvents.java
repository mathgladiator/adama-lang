package org.adamalang.disk.files;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** since a snapshot file may be large, we stream it via events */
public interface SnapshotFileStreamEvents {
  /** header; return true if you want to continue reading */
  public boolean onHeader(SnapshotHeader header) throws IOException;

  /** snapshot of the document (after header); return true if you want to continue reading */
  public boolean onDocument(String document) throws IOException;

  /** undo (after documents and undo); return true if you want to continue reading */
  public boolean onUndo(int seq, String undo) throws IOException;

  /** finished */
  public void onFinished(boolean complete) throws IOException;

  /** stream a snapshot file from a DataInputStream */
  public static void read(DataInputStream data, SnapshotFileStreamEvents events) throws IOException {
    SnapshotHeader header = SnapshotHeader.read(data);
    if (events.onHeader(header)) {
      byte[] document = new byte[header.documentSize];
      if (events.onDocument(new String(document, StandardCharsets.UTF_8))) {
        while (data.readBoolean()) {
          int seq = data.readInt();
          byte[] undo = new byte[data.readInt()];
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

  /** create a writer to produce a snapshot file */
  public static SnapshotFileStreamEvents writerFor(DataOutputStream output) {
    return new SnapshotFileStreamEvents() {
      @Override
      public boolean onHeader(SnapshotHeader header) throws IOException {
        header.write(output);
        return true;
      }

      @Override
      public boolean onDocument(String document) throws IOException {
        output.write(document.getBytes(StandardCharsets.UTF_8));
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
        output.close();
      }
    };
  }
}
