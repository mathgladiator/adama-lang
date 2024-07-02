/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

  @Override
  public void handle(Events.Recover payload) {
    scanJson(payload.document);
  }

  public void scanJson(String json) {
    new JsonStreamReader(json).populateGarbageCollectedIds(ids);
  }
}
