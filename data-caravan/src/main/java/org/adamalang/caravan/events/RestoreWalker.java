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

  @Override
  public void handle(Events.Recover payload) {
    seq = payload.seq;
    assetBytes = 0L;
  }
}
