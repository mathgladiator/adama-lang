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

import java.io.*;

/** the header contains useful information regarding the snapshot */
public class SnapshotHeader {
  public final int seq;
  public final int history;
  public final int documentSize;
  public final boolean active;
  public final long assetBytes;

  public SnapshotHeader(int seq, int history, int documentSize, long assetBytes, boolean active) {
    this.seq = seq;
    this.history = history;
    this.documentSize = documentSize;
    this.assetBytes = assetBytes;
    this.active = active;
  }

  /** read the header from the data stream */
  public static SnapshotHeader read(DataInputStream data) throws IOException {
    data.readInt();
    int seq = data.readInt();
    int history = data.readInt();
    int documentSize = data.readInt();
    long assetBytes = data.readLong();
    boolean active = data.readBoolean();
    return new SnapshotHeader(seq, history, documentSize, assetBytes, active);
  }

  /** write the header to the output stream */
  public void write(DataOutputStream data) throws IOException {
    data.writeInt(0x424213);
    data.writeInt(seq);
    data.writeInt(history);
    data.writeInt(documentSize);
    data.writeLong(assetBytes);
    data.writeBoolean(active);
  }

  @Override
  public String toString() {
    return "SnapshotHeader{" + "seq=" + seq + ", history=" + history + ", documentSize=" + documentSize + ", active=" + active + ", assetBytes=" + assetBytes + '}';
  }
}
