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
import org.adamalang.runtime.json.JsonStreamReader;

import java.util.ArrayList;
import java.util.HashSet;

/** walk assets within a restore file and produce the ids */
public class AssetWalker implements EventCodec.HandlerEvent {
  private final HashSet<String> ids;

  public AssetWalker() {
    this.ids = new HashSet<>();
  }

  /** entry point: give it a list of writes and get a list of asset ids */
  public static HashSet<String> idsOf(ArrayList<byte[]> writes) {
    AssetWalker walker = new AssetWalker();
    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), walker);
    }
    return walker.ids;
  }

  @Override
  public void handle(Events.Snapshot payload) {
    scanJson(payload.document);
  }

  @Override
  public void handle(Events.Batch payload) {
    for (Events.Change change : payload.changes) {
      handle(change);
    }
  }

  @Override
  public void handle(Events.Change payload) {
    scanJson(payload.redo);
    scanJson(payload.undo);
  }

  public void scanJson(String json) {
    new JsonStreamReader(json).populateGarbageCollectedIds(ids);
  }
}
