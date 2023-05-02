/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.events;

/** useful for parsing some byte[] and extracting the seq and assets bytes */
public class RestoreWalker implements EventCodec.HandlerEvent {
  public int seq;
  public long assetBytes;

  public RestoreWalker() {
    this.seq = 0;
    this.assetBytes = 0L;
  }

  @Override
  public void handle(Events.Snapshot payload) {
    seq = Math.max(payload.seq, seq);
    assetBytes += payload.assetBytes;
  }

  @Override
  public void handle(Events.Batch payload) {
    for (Events.Change change : payload.changes) {
      handle(change);
    }
  }

  @Override
  public void handle(Events.Change payload) {
    seq = Math.max(payload.seq_end, seq);
    assetBytes += payload.dAssetBytes;
  }
}
