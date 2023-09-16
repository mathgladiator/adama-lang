/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
