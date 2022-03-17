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
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.disk.wal.WriteAheadMessage.*;

public class WriteAheadMessageCodec {

  public static void route(ByteBuf buf, HandlerWAL handler) {
    switch (buf.readIntLE()) {
      case 48:
        handler.handle(readBody_48(buf, new Snapshot()));
        return;
      case 37:
        handler.handle(readBody_37(buf, new Delete()));
        return;
      case 32:
        handler.handle(readBody_32(buf, new Patch()));
        return;
      case 5:
        handler.handle(readBody_5(buf, new Initialize()));
        return;
    }
  }

  public static void route(ByteBuf buf, HandlerPatchItem handler) {
    switch (buf.readIntLE()) {
      case 21:
        handler.handle(readBody_21(buf, new Change()));
        return;
    }
  }

  private static Change readBody_21(ByteBuf buf, Change o) {
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.seq_begin = buf.readIntLE();
    o.seq_end = buf.readIntLE();
    o.request = Helper.readString(buf);
    o.redo = Helper.readString(buf);
    o.undo = Helper.readString(buf);
    o.active = buf.readBoolean();
    o.delay = buf.readIntLE();
    o.dAssetBytes = buf.readLongLE();
    return o;
  }

  public static WriteAheadMessage read_WriteAheadMessage(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 48:
        return readBody_48(buf, new Snapshot());
      case 37:
        return readBody_37(buf, new Delete());
      case 32:
        return readBody_32(buf, new Patch());
      case 5:
        return readBody_5(buf, new Initialize());
    }
    return null;
  }

  public static Snapshot read_Snapshot(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 48:
        return readBody_48(buf, new Snapshot());
    }
    return null;
  }

  private static Snapshot readBody_48(ByteBuf buf, Snapshot o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.seq = buf.readIntLE();
    o.history = buf.readIntLE();
    o.document = Helper.readString(buf);
    return o;
  }

  public static Delete read_Delete(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 37:
        return readBody_37(buf, new Delete());
    }
    return null;
  }

  private static Delete readBody_37(ByteBuf buf, Delete o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    return o;
  }

  public static Patch read_Patch(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 32:
        return readBody_32(buf, new Patch());
    }
    return null;
  }

  private static Patch readBody_32(ByteBuf buf, Patch o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.changes = Helper.readArray(buf, (n) -> new Change[n], () -> read_Change(buf));
    return o;
  }

  public static Change read_Change(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 21:
        return readBody_21(buf, new Change());
    }
    return null;
  }

  public static Initialize read_Initialize(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 5:
        return readBody_5(buf, new Initialize());
    }
    return null;
  }

  private static Initialize readBody_5(ByteBuf buf, Initialize o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.initialize = read_Change(buf);
    return o;
  }

  public static void write(ByteBuf buf, Snapshot o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(48);
    Helper.writeString(buf, o.space);
    Helper.writeString(buf, o.key);
    buf.writeIntLE(o.seq);
    buf.writeIntLE(o.history);
    Helper.writeString(buf, o.document);
  }

  public static void write(ByteBuf buf, Delete o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(37);
    Helper.writeString(buf, o.space);
    Helper.writeString(buf, o.key);
  }

  public static void write(ByteBuf buf, Patch o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(32);
    Helper.writeString(buf, o.space);
    Helper.writeString(buf, o.key);
    Helper.writeArray(buf, o.changes, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, Change o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(21);
    Helper.writeString(buf, o.agent);
    Helper.writeString(buf, o.authority);
    buf.writeIntLE(o.seq_begin);
    buf.writeIntLE(o.seq_end);
    Helper.writeString(buf, o.request);
    Helper.writeString(buf, o.redo);
    Helper.writeString(buf, o.undo);
    buf.writeBoolean(o.active);
    buf.writeIntLE(o.delay);
    buf.writeLongLE(o.dAssetBytes);
  }

  public static void write(ByteBuf buf, Initialize o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(5);
    Helper.writeString(buf, o.space);
    Helper.writeString(buf, o.key);
    write(buf, o.initialize);
  }

  public interface HandlerWAL {
    void handle(Snapshot payload);

    void handle(Delete payload);

    void handle(Patch payload);

    void handle(Initialize payload);
  }

  public interface HandlerPatchItem {
    void handle(Change payload);
  }

  public static abstract class StreamWAL implements ByteStream {
    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 48:
          handle(readBody_48(buf, new Snapshot()));
          return;
        case 37:
          handle(readBody_37(buf, new Delete()));
          return;
        case 32:
          handle(readBody_32(buf, new Patch()));
          return;
        case 5:
          handle(readBody_5(buf, new Initialize()));
          return;
      }
    }

    public abstract void handle(Snapshot payload);

    public abstract void handle(Delete payload);

    public abstract void handle(Patch payload);

    public abstract void handle(Initialize payload);
  }

  public static abstract class StreamPatchItem implements ByteStream {
    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 21:
          handle(readBody_21(buf, new Change()));
          return;
      }
    }

    public abstract void handle(Change payload);
  }
}
