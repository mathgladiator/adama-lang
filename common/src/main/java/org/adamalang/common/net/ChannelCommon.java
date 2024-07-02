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
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.ErrorCodes;
import org.adamalang.common.gossip.Engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** since both the server and client share some common things, we group them here */
public abstract class ChannelCommon extends ChannelInboundHandlerAdapter {
  protected final HashMap<Integer, ByteStream> streams;
  protected final Engine gossipEngine;
  private final int initialId;
  protected ScheduledFuture<?> scheduledFlush;
  private int nextId;

  public ChannelCommon(int initialId, Engine gossipEngine) {
    this.initialId = initialId;
    this.scheduledFlush = null;
    this.streams = new HashMap<>();
    this.nextId = initialId;
    this.gossipEngine = gossipEngine;
  }

  public int makeId() {
    while (streams.containsKey(nextId)) {
      nextId += 2;
    }
    int id = nextId;
    nextId += 2;
    if (nextId >= 4194304) {
      nextId = initialId;
    }
    return id;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ctx.executor().execute(() -> {
      if (scheduledFlush != null) {
        scheduledFlush.cancel(true);
        scheduledFlush = null;
      }
      for (Map.Entry<Integer, ByteStream> stream : streams.entrySet()) {
        stream.getValue().error(ErrorCodes.NET_DISCONNECT);
      }
      streams.clear();
    });
  }

  protected void routeCommon(byte type, int id, ByteBuf inBuffer, ChannelHandlerContext ctx) {
    switch (type) {
      case 0x20: {
        ByteStream stream = streams.get(id);
        if (stream != null) {
          stream.next(inBuffer);
        }
      }
      break;
      case 0x30: {
        ByteStream stream = streams.get(id);
        if (stream != null) {
          stream.request(inBuffer.readIntLE());
        }
      }
      break;
      case 0x40: {
        ByteStream stream = streams.remove(id);
        if (stream != null) {
          stream.completed();
        }
        sendConfirmRemoval(ctx, id);
      }
      break;
      case 0x50: {
        ByteStream stream = streams.remove(id);
        if (stream != null) {
          stream.error(inBuffer.readIntLE());
        }
      }
      break;
      case 0x60: {
        ByteStream stream = streams.remove(id);
        if (stream != null) {
          stream.completed();
        }
      }
      break;
    }
  }

  protected void sendConfirmRemoval(ChannelHandlerContext context, int id) {
    context.executor().execute(() -> {
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(0x60);
      buffer.writeIntLE(id);
      context.write(buffer);
      flushFromWithinContextExecutor(context);
    });
  }

  protected void flushFromWithinContextExecutor(ChannelHandlerContext context) {
    if (scheduledFlush != null) {
      return;
    }
    scheduledFlush = context.executor().schedule(() -> {
      context.flush();
      scheduledFlush = null;
    }, 50000, TimeUnit.NANOSECONDS);
  }
}
