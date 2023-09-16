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
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/** a callback to isolate a single sub-channel within a channel */
public class Remote implements ByteStream {
  private final HashMap<Integer, ByteStream> streams;
  private final int id;
  private final ChannelHandlerContext context;
  private final Runnable requestFlush;

  public Remote(HashMap<Integer, ByteStream> streams, int id, ChannelHandlerContext context, Runnable requestFlush) {
    this.streams = streams;
    this.id = id;
    this.context = context;
    this.requestFlush = requestFlush;
  }

  @Override
  public void request(int bytes) {
    context.executor().execute(() -> {
      ByteBuf request = Unpooled.buffer();
      request.writeByte(0x30);
      request.writeIntLE(id);
      request.writeIntLE(bytes);
      context.write(request);
      requestFlush.run();
    });
  }

  @Override
  public ByteBuf create(int bestGuessForSize) {
    ByteBuf request = Unpooled.buffer();
    request.writeByte(0x20);
    request.writeIntLE(id);
    return request;
  }

  @Override
  public void next(ByteBuf buf) {
    context.executor().execute(() -> {
      context.write(buf);
      requestFlush.run();
    });
  }

  @Override
  public void completed() {
    context.executor().execute(() -> {
      ByteBuf request = Unpooled.buffer();
      request.writeByte(0x40);
      request.writeIntLE(id);
      context.write(request);
      requestFlush.run();
    });
  }

  @Override
  public void error(int errorCode) {
    context.executor().execute(() -> {
      streams.remove(id);
      ByteBuf request = Unpooled.buffer();
      request.writeByte(0x50);
      request.writeIntLE(id);
      request.writeIntLE(errorCode);
      context.write(request);
      requestFlush.run();
    });
  }
}
