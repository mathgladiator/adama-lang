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

import org.adamalang.common.codec.CodecCodeGen;
import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;
import org.adamalang.runtime.data.RemoteDocumentUpdate;

import java.io.File;
import java.nio.file.Files;

public interface Events {
  static void main(String[] args) throws Exception {
    String codec = CodecCodeGen.assembleCodec("org.adamalang.caravan.events", "EventCodec", Events.class.getDeclaredClasses());
    Files.writeString(new File("./data-caravan/src/main/java/org/adamalang/caravan/events/EventCodec.java").toPath(), codec);
  }

  @TypeId(0x10)
  @Flow("Event")
  class Change {
    @FieldOrder(1)
    public String agent;
    @FieldOrder(2)
    public String authority;
    @FieldOrder(3)
    public int seq_begin;
    @FieldOrder(4)
    public int seq_end;
    @FieldOrder(5)
    public String request;
    @FieldOrder(6)
    public String redo;
    @FieldOrder(7)
    public String undo;
    @FieldOrder(8)
    public boolean active;
    @FieldOrder(9)
    public int delay;
    @FieldOrder(10)
    public long dAssetBytes;

    public void copyFrom(RemoteDocumentUpdate update) {
      this.seq_begin = update.seqBegin;
      this.seq_end = update.seqEnd;
      if (update.who != null) {
        this.agent = update.who.agent;
        this.authority = update.who.authority;
      }
      this.request = update.request;
      this.redo = update.redo;
      this.undo = update.undo;
      this.active = update.requiresFutureInvalidation;
      this.delay = update.whenToInvalidateMilliseconds;
      this.dAssetBytes = update.assetBytes;
    }
  }

  @TypeId(0x20)
  @Flow("Event")
  class Batch {
    @FieldOrder(1)
    public Change[] changes;
  }

  @TypeId(0x30)
  @Flow("Event")
  class Snapshot {
    @FieldOrder(1)
    public int seq;
    @FieldOrder(2)
    public int history;
    @FieldOrder(3)
    public String document;
    @FieldOrder(4)
    public long assetBytes;
  }
}
