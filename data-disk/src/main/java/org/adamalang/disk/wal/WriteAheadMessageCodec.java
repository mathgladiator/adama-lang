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
import org.adamalang.disk.wal.WriteAheadMessage.Snapshot;
import org.adamalang.disk.wal.WriteAheadMessage.Delete;
import org.adamalang.disk.wal.WriteAheadMessage.Patch;
import org.adamalang.disk.wal.WriteAheadMessage.Change;
import org.adamalang.disk.wal.WriteAheadMessage.Compact;
import org.adamalang.disk.wal.WriteAheadMessage.Initialize;

public class WriteAheadMessageCodec {

  public static abstract class StreamWAL implements ByteStream {
    public abstract void handle(Snapshot payload);

    public abstract void handle(Delete payload);

    public abstract void handle(Patch payload);

    public abstract void handle(Compact payload);

    public abstract void handle(Initialize payload);

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
        case 16:
          handle(readBody_16(buf, new Compact()));
          return;
        case 5:
          handle(readBody_5(buf, new Initialize()));
          return;
      }
    }
  }

  public static interface HandlerWAL {
    public void handle(Snapshot payload);
    public void handle(Delete payload);
    public void handle(Patch payload);
    public void handle(Compact payload);
    public void handle(Initialize payload);
  }

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
      case 16:
        handler.handle(readBody_16(buf, new Compact()));
        return;
      case 5:
        handler.handle(readBody_5(buf, new Initialize()));
        return;
    }
  }

  public static abstract class StreamPatchItem implements ByteStream {
    public abstract void handle(Change payload);

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
  }

  public static interface HandlerPatchItem {
    public void handle(Change payload);
  }

  public static void route(ByteBuf buf, HandlerPatchItem handler) {
    switch (buf.readIntLE()) {
      case 21:
        handler.handle(readBody_21(buf, new Change()));
        return;
    }
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


  private static Change readBody_21(ByteBuf buf, Change o) {
    o.seq_begin = buf.readIntLE();
    o.seq_end = buf.readIntLE();
    o.request = Helper.readString(buf);
    o.redo = Helper.readString(buf);
    o.undo = Helper.readString(buf);
    return o;
  }

  public static Compact read_Compact(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 16:
        return readBody_16(buf, new Compact());
    }
    return null;
  }


  private static Compact readBody_16(ByteBuf buf, Compact o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.history = buf.readIntLE();
    return o;
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
    return o;
  }

  public static void write(ByteBuf buf, Snapshot o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(48);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    buf.writeIntLE(o.history);
    Helper.writeString(buf, o.document);;
  }

  public static void write(ByteBuf buf, Delete o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(37);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
  }

  public static void write(ByteBuf buf, Patch o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(32);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeArray(buf, o.changes, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, Change o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(21);
    buf.writeIntLE(o.seq_begin);
    buf.writeIntLE(o.seq_end);
    Helper.writeString(buf, o.request);;
    Helper.writeString(buf, o.redo);;
    Helper.writeString(buf, o.undo);;
  }

  public static void write(ByteBuf buf, Compact o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(16);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    buf.writeIntLE(o.history);
  }

  public static void write(ByteBuf buf, Initialize o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(5);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
  }
}
