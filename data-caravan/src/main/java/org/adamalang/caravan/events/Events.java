/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
