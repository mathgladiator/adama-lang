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
package org.adamalang.train.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.train.message.Messages.LogGossipStart;
import org.adamalang.train.message.Messages.VoteResponse;
import org.adamalang.train.message.Messages.VoteRequest;
import org.adamalang.train.message.Messages.AppendEntriesResponse;
import org.adamalang.train.message.Messages.AppendEntriesRequest;
import org.adamalang.train.message.Messages.Entry;

public class MessagesCodec {

  public static abstract class StreamServer implements ByteStream {
    public abstract void handle(VoteResponse payload);

    public abstract void handle(AppendEntriesResponse payload);

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
        case 2001:
          handle(readBody_2001(buf, new VoteResponse()));
          return;
        case 1001:
          handle(readBody_1001(buf, new AppendEntriesResponse()));
          return;
      }
    }
  }

  public static interface HandlerServer {
    public void handle(VoteResponse payload);
    public void handle(AppendEntriesResponse payload);
  }

  public static void route(ByteBuf buf, HandlerServer handler) {
    switch (buf.readIntLE()) {
      case 2001:
        handler.handle(readBody_2001(buf, new VoteResponse()));
        return;
      case 1001:
        handler.handle(readBody_1001(buf, new AppendEntriesResponse()));
        return;
    }
  }


  public static abstract class StreamData implements ByteStream {
    public abstract void handle(Entry payload);

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
        case 500:
          handle(readBody_500(buf, new Entry()));
          return;
      }
    }
  }

  public static interface HandlerData {
    public void handle(Entry payload);
  }

  public static void route(ByteBuf buf, HandlerData handler) {
    switch (buf.readIntLE()) {
      case 500:
        handler.handle(readBody_500(buf, new Entry()));
        return;
    }
  }


  public static abstract class StreamClient implements ByteStream {
    public abstract void handle(LogGossipStart payload);

    public abstract void handle(VoteRequest payload);

    public abstract void handle(AppendEntriesRequest payload);

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
        case 3001:
          handle(readBody_3001(buf, new LogGossipStart()));
          return;
        case 2000:
          handle(readBody_2000(buf, new VoteRequest()));
          return;
        case 1000:
          handle(readBody_1000(buf, new AppendEntriesRequest()));
          return;
      }
    }
  }

  public static interface HandlerClient {
    public void handle(LogGossipStart payload);
    public void handle(VoteRequest payload);
    public void handle(AppendEntriesRequest payload);
  }

  public static void route(ByteBuf buf, HandlerClient handler) {
    switch (buf.readIntLE()) {
      case 3001:
        handler.handle(readBody_3001(buf, new LogGossipStart()));
        return;
      case 2000:
        handler.handle(readBody_2000(buf, new VoteRequest()));
        return;
      case 1000:
        handler.handle(readBody_1000(buf, new AppendEntriesRequest()));
        return;
    }
  }


  public static LogGossipStart read_LogGossipStart(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 3001:
        return readBody_3001(buf, new LogGossipStart());
    }
    return null;
  }


  private static LogGossipStart readBody_3001(ByteBuf buf, LogGossipStart o) {
    o.logId = buf.readIntLE();
    return o;
  }

  public static VoteResponse read_VoteResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2001:
        return readBody_2001(buf, new VoteResponse());
    }
    return null;
  }


  private static VoteResponse readBody_2001(ByteBuf buf, VoteResponse o) {
    o.term = buf.readLongLE();
    o.voteGranted = buf.readBoolean();
    return o;
  }

  public static VoteRequest read_VoteRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2000:
        return readBody_2000(buf, new VoteRequest());
    }
    return null;
  }


  private static VoteRequest readBody_2000(ByteBuf buf, VoteRequest o) {
    o.logId = buf.readIntLE();
    o.term = buf.readLongLE();
    o.candidateId = buf.readIntLE();
    o.lastLogIndex = buf.readIntLE();
    o.lastLogTerm = buf.readLongLE();
    return o;
  }

  public static AppendEntriesResponse read_AppendEntriesResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1001:
        return readBody_1001(buf, new AppendEntriesResponse());
    }
    return null;
  }


  private static AppendEntriesResponse readBody_1001(ByteBuf buf, AppendEntriesResponse o) {
    o.term = buf.readLongLE();
    o.accepted = buf.readBoolean();
    return o;
  }

  public static AppendEntriesRequest read_AppendEntriesRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1000:
        return readBody_1000(buf, new AppendEntriesRequest());
    }
    return null;
  }


  private static AppendEntriesRequest readBody_1000(ByteBuf buf, AppendEntriesRequest o) {
    o.logId = buf.readIntLE();
    o.term = buf.readLongLE();
    o.leaderId = buf.readIntLE();
    o.prevLogTerm = buf.readLongLE();
    o.entries = Helper.readArray(buf, (n) -> new Entry[n], () -> read_Entry(buf));
    o.commitIndex = buf.readLongLE();
    return o;
  }

  public static Entry read_Entry(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 500:
        return readBody_500(buf, new Entry());
    }
    return null;
  }


  private static Entry readBody_500(ByteBuf buf, Entry o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.seqBegin = buf.readIntLE();
    o.seqEnd = buf.readIntLE();
    return o;
  }

  public static void write(ByteBuf buf, LogGossipStart o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(3001);
    buf.writeIntLE(o.logId);
  }

  public static void write(ByteBuf buf, VoteResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2001);
    buf.writeLongLE(o.term);
    buf.writeBoolean(o.voteGranted);
  }

  public static void write(ByteBuf buf, VoteRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2000);
    buf.writeIntLE(o.logId);
    buf.writeLongLE(o.term);
    buf.writeIntLE(o.candidateId);
    buf.writeIntLE(o.lastLogIndex);
    buf.writeLongLE(o.lastLogTerm);
  }

  public static void write(ByteBuf buf, AppendEntriesResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1001);
    buf.writeLongLE(o.term);
    buf.writeBoolean(o.accepted);
  }

  public static void write(ByteBuf buf, AppendEntriesRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1000);
    buf.writeIntLE(o.logId);
    buf.writeLongLE(o.term);
    buf.writeIntLE(o.leaderId);
    buf.writeLongLE(o.prevLogTerm);
    Helper.writeArray(buf, o.entries, (item) -> write(buf, item));
    buf.writeLongLE(o.commitIndex);
  }

  public static void write(ByteBuf buf, Entry o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(500);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    buf.writeIntLE(o.seqBegin);
    buf.writeIntLE(o.seqEnd);
  }
}
