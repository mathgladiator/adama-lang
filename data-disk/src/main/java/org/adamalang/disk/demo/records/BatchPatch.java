/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.demo.records;

import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** codec for a batch of patches */
public class BatchPatch {
  private static void writeString(DataOutputStream output, String x) throws IOException {
    output.writeInt(x.length());
    output.write(x.getBytes(StandardCharsets.UTF_8));
  }

  public static String readString(DataInputStream input) throws IOException {
    byte[] b = new byte[input.readInt()];
    input.readFully(b);
    return new String(b, StandardCharsets.UTF_8);
  }

  public static void write(RemoteDocumentUpdate[] updates, DataOutputStream output) throws IOException {
    output.write(0x99);
    output.writeInt(updates.length);
    for (int k = 0; k < updates.length; k++) {
      RemoteDocumentUpdate update = updates[k];
      writeString(output, update.request);
      writeString(output, update.redo);
      writeString(output, update.undo);
      output.writeInt(update.seqBegin);
      output.writeInt(update.seqEnd);
      output.writeBoolean(update.who != null);
      if (update.who != null) {
        writeString(output, update.who.agent);
        writeString(output, update.who.authority);
      }
      output.writeBoolean(update.requiresFutureInvalidation);
      output.writeInt(update.whenToInvalidateMilliseconds);
      output.writeLong(update.assetBytes);
    }
  }

  public static RemoteDocumentUpdate[] read(int version, DataInputStream input) throws IOException {
    if (input.read() < 0) {
      return null;
    }
    int size = input.readInt();
    RemoteDocumentUpdate[] updates = new RemoteDocumentUpdate[size];
    for (int k = 0; k < updates.length; k++) {
      String request = readString(input);
      String redo = readString(input);
      String undo = readString(input);
      int seqBegin = input.readInt();
      int seqEnd = input.readInt();
      NtClient who = null;
      if (input.readBoolean()) {
        String agent = readString(input);
        String authority = readString(input);
        who = new NtClient(agent, authority);
      }
      boolean requiresFutureInvalidation = input.readBoolean();
      int whenToInvalidateMilliseconds = input.readInt();
      long assetBytes = input.readLong();
      updates[k] = new RemoteDocumentUpdate(seqBegin, seqEnd, who, request, redo, undo, requiresFutureInvalidation, whenToInvalidateMilliseconds, assetBytes, UpdateType.Internal);
    }
    return updates;
  }
}
