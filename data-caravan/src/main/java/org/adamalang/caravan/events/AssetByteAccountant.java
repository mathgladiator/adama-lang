/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.events;

import io.netty.buffer.Unpooled;

/** Since a restore has both deltas and snapshots, we need the snapshots to reset the counter */
public class AssetByteAccountant implements EventCodec.HandlerEvent {
  private long bytes;

  public AssetByteAccountant() {
    this.bytes = 0;
  }

  public long getBytes() {
    return bytes;
  }

  public void account(byte[] entry, long size) {
    EventCodec.route(Unpooled.wrappedBuffer(entry), this);
    bytes += size;
  }

  @Override
  public void handle(Events.Snapshot payload) {
    bytes = 0;
  }

  @Override
  public void handle(Events.Batch payload) {
  }

  @Override
  public void handle(Events.Change payload) {
  }
}
