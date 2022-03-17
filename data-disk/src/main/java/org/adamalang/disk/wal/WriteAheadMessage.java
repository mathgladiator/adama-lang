/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.wal;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeCommon;
import org.adamalang.common.codec.TypeId;
import org.adamalang.disk.DocumentMemoryLog;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;

public interface WriteAheadMessage {
  void write(ByteBuf buf);

  void apply(DocumentMemoryLog log);

  Key key();

  boolean requiresLoad();

  @TypeId(0x05)
  @Flow("WAL")
  @TypeCommon("WriteAheadMessage")
  class Initialize implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public Change initialize;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }

    @Override
    public void apply(DocumentMemoryLog log) {
      log.apply(this);
    }

    @Override
    public Key key() {
      return new Key(space, key);
    }

    @Override
    public boolean requiresLoad() {
      return false;
    }
  }

  @TypeId(0x15)
  @Flow("PatchItem")
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
  @Flow("WAL")
  @TypeCommon("WriteAheadMessage")
  class Patch implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public Change[] changes;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }

    @Override
    public void apply(DocumentMemoryLog log) {
      log.apply(this);
    }

    @Override
    public Key key() {
      return new Key(space, key);
    }

    @Override
    public boolean requiresLoad() {
      return true;
    }
  }

  @TypeId(0x25)
  @Flow("WAL")
  @TypeCommon("WriteAheadMessage")
  class Delete implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }

    @Override
    public void apply(DocumentMemoryLog log) {
      log.delete();
    }

    @Override
    public Key key() {
      return new Key(space, key);
    }

    @Override
    public boolean requiresLoad() {
      return false;
    }
  }

  @TypeId(0x30)
  @Flow("WAL")
  @TypeCommon("WriteAheadMessage")
  class Snapshot implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public int seq;
    @FieldOrder(4)
    public int history;
    @FieldOrder(5)
    public String document;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }

    @Override
    public void apply(DocumentMemoryLog log) {
      log.apply(this);
    }

    @Override
    public Key key() {
      return new Key(space, key);
    }

    @Override
    public boolean requiresLoad() {
      return false;
    }
  }
}
