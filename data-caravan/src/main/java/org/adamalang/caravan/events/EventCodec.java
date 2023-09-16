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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.caravan.events.Events.Snapshot;
import org.adamalang.caravan.events.Events.Batch;
import org.adamalang.caravan.events.Events.Change;

public class EventCodec {

  public static abstract class StreamEvent implements ByteStream {
    public abstract void handle(Snapshot payload);

    public abstract void handle(Batch payload);

    public abstract void handle(Change payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 48:
          handle(readBody_48(buf, new Snapshot()));
          return;
        case 32:
          handle(readBody_32(buf, new Batch()));
          return;
        case 16:
          handle(readBody_16(buf, new Change()));
          return;
      }
    }
  }

  public static interface HandlerEvent {
    public void handle(Snapshot payload);
    public void handle(Batch payload);
    public void handle(Change payload);
  }

  public static void route(ByteBuf buf, HandlerEvent handler) {
    switch (buf.readIntLE()) {
      case 48:
        handler.handle(readBody_48(buf, new Snapshot()));
        return;
      case 32:
        handler.handle(readBody_32(buf, new Batch()));
        return;
      case 16:
        handler.handle(readBody_16(buf, new Change()));
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
    o.seq = buf.readIntLE();
    o.history = buf.readIntLE();
    o.document = Helper.readString(buf);
    o.assetBytes = buf.readLongLE();
    return o;
  }

  public static Batch read_Batch(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 32:
        return readBody_32(buf, new Batch());
    }
    return null;
  }


  private static Batch readBody_32(ByteBuf buf, Batch o) {
    o.changes = Helper.readArray(buf, (n) -> new Change[n], () -> read_Change(buf));
    return o;
  }

  public static Change read_Change(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 16:
        return readBody_16(buf, new Change());
    }
    return null;
  }


  private static Change readBody_16(ByteBuf buf, Change o) {
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

  public static void write(ByteBuf buf, Snapshot o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(48);
    buf.writeIntLE(o.seq);
    buf.writeIntLE(o.history);
    Helper.writeString(buf, o.document);;
    buf.writeLongLE(o.assetBytes);
  }

  public static void write(ByteBuf buf, Batch o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(32);
    Helper.writeArray(buf, o.changes, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, Change o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(16);
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    buf.writeIntLE(o.seq_begin);
    buf.writeIntLE(o.seq_end);
    Helper.writeString(buf, o.request);;
    Helper.writeString(buf, o.redo);;
    Helper.writeString(buf, o.undo);;
    buf.writeBoolean(o.active);
    buf.writeIntLE(o.delay);
    buf.writeLongLE(o.dAssetBytes);
  }
}
