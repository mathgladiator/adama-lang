/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
