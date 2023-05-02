/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

/** a snapshot of a document at a specific sequencer */
public class DocumentSnapshot {
  public final int seq;
  public final String json;
  public final int history;
  public final long assetBytes;

  public DocumentSnapshot(int seq, String json, int history, long assetBytes) {
    this.seq = seq;
    this.json = json;
    this.history = history;
    this.assetBytes = assetBytes;
  }
}
