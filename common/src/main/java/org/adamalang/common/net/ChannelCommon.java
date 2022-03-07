/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.ErrorCodes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** since both the server and client share some common things, we group them here */
public abstract class ChannelCommon extends ChannelInboundHandlerAdapter  {
  protected final HashMap<Integer, ByteStream> streams;
  protected ScheduledFuture<?> scheduledFlush;
  private final int initialId;
  private int nextId;

  public ChannelCommon(int initialId) {
    this.initialId = initialId;
    this.scheduledFlush = null;
    this.streams = new HashMap<>();
    this.nextId = initialId;
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
    if (scheduledFlush != null) {
      scheduledFlush.cancel(true);
      scheduledFlush = null;
    }

    for (Map.Entry<Integer, ByteStream> stream : streams.entrySet()) {
      stream.getValue().error(ErrorCodes.NET_DISCONNECT);
    }
    streams.clear();
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

  protected void sendConfirmRemoval(ChannelHandlerContext context, int id) {
    context.executor().execute(() -> {
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(0x60);
      buffer.writeIntLE(id);
      context.write(buffer);
      flushFromWithinContextExecutor(context);
    });
  }

  protected void routeCommon(byte type, int id, ByteBuf inBuffer, ChannelHandlerContext ctx) {
    switch (type) {
      case 0x20: {
        ByteStream stream = streams.get(id);
        if (stream != null) {
          stream.next(inBuffer);
        }
      } break;
      case 0x30: {
        ByteStream stream = streams.get(id);
        if (stream != null) {
          stream.request(inBuffer.readIntLE());
        }
      } break;
      case 0x40: {
        ByteStream stream = streams.remove(id);
        if (stream != null) {
          stream.completed();
        }
        sendConfirmRemoval(ctx, id);
      } break;
      case 0x50: {
        ByteStream stream = streams.remove(id);
        if (stream != null) {
          stream.error(inBuffer.readIntLE());
        }
      } break;
      case 0x60: {
        streams.remove(id);
      } break;
    }
  }
}
