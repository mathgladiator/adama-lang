/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.events;

import io.netty.buffer.Unpooled;

import java.util.ArrayList;

public class RestoreDebuggerStdErr implements EventCodec.HandlerEvent {
  @Override
  public void handle(Events.Snapshot payload) {
    System.err.println("Snapshot["+payload.seq+"]:" + payload.document);
  }

  @Override
  public void handle(Events.Batch payload) {
    if (payload.changes.length == 1) {
      handle(payload.changes[0]);
      return;
    }
    System.err.println("Batch:" + payload.changes.length);
    int index = 0;
    for (Events.Change change : payload.changes) {
      System.err.print("[" + index + "] ");
      handle(change);
      index++;
    }
  }

  @Override
  public void handle(Events.Change payload) {
    System.err.println("Change[" + payload.seq_begin + "->" + payload.seq_end + "," + payload.dAssetBytes + "] = " + payload.redo);
  }

  public static void print(ArrayList<byte[]> writes) {
    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), new RestoreDebuggerStdErr());
    }
  }
}
