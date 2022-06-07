/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
