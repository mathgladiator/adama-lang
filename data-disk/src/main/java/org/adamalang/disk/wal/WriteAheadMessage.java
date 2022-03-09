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
import org.adamalang.common.codec.TypeId;

public interface WriteAheadMessage {
  public void write(ByteBuf buf);

  @TypeId(0x05)
  @Flow("WAL")
  public static class Initialize implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }
  }

  @TypeId(0x10)
  @Flow("WAL")
  public static class Compact implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public int history;
    @FieldOrder(4)
    public String patch;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }
  }

  @TypeId(0x15)
  @Flow("PatchItem")
  public static class Change implements WriteAheadMessage {
    @FieldOrder(1)
    public int seq_begin;
    @FieldOrder(2)
    public int seq_end;
    @FieldOrder(3)
    public String request;
    @FieldOrder(4)
    public String redo;
    @FieldOrder(5)
    public String undo;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }
  }

  @TypeId(0x20)
  @Flow("WAL")
  public static class Patch implements WriteAheadMessage {
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
  }

  @TypeId(0x25)
  @Flow("WAL")
  public static class Delete implements WriteAheadMessage {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;

    @Override
    public void write(ByteBuf buf) {
      WriteAheadMessageCodec.write(buf, this);
    }
  }

  @TypeId(0x30)
  @Flow("WAL")
  public static class Snapshot implements WriteAheadMessage {
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
  }
}
