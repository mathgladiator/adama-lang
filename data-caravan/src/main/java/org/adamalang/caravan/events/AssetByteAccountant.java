/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
}
