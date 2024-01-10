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

/** Since a restore has both deltas and snapshots, we need the snapshots to reset the counter */
public class AssetByteAccountant implements EventCodec.HandlerEvent {
  private long bytes;
  private boolean snapshot;
  private int minimumSeq;

  public AssetByteAccountant() {
    this.bytes = 0;
    this.snapshot = false;
    this.minimumSeq = Integer.MAX_VALUE;
  }

  public long getBytes() {
    return bytes;
  }

  public boolean hasThereBeenDataloss() {
    return minimumSeq > 1 && !snapshot;
  }

  public void account(byte[] entry, long size) {
    EventCodec.route(Unpooled.wrappedBuffer(entry), this);
    bytes += size;
  }

  @Override
  public void handle(Events.Snapshot payload) {
    bytes = 0;
    snapshot = true;
  }

  @Override
  public void handle(Events.Batch payload) {
    for (Events.Change change : payload.changes) {
      handle(change);
    }
  }

  @Override
  public void handle(Events.Change payload) {
    if (payload.seq_begin < minimumSeq) {
      minimumSeq = payload.seq_begin;
    }
  }

  @Override
  public void handle(Events.Recover payload) {
    bytes = 0;
    snapshot = true;
    minimumSeq = payload.seq;
  }
}
