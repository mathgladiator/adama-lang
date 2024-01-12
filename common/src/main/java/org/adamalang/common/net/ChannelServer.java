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
import io.netty.channel.socket.SocketChannel;
import org.adamalang.common.gossip.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** the server side of the connection */
public class ChannelServer extends ChannelCommon {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChannelServer.class);
  private final SocketChannelSet set;
  private final Handler handler;
  private final int id;

  public ChannelServer(SocketChannel socket, SocketChannelSet set, Handler handler, Engine gossipEngine) {
    super(2, gossipEngine);
    this.set = set;
    this.handler = handler;
    this.id = set.add(socket);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    set.remove(this.id);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf inBuffer = (ByteBuf) msg;
    byte type = inBuffer.readByte();
    int id = inBuffer.readIntLE();
    if (type == 0x10 || type == 0x11) {
      ByteStream upstream = new Remote(streams, id, ctx, () -> flushFromWithinContextExecutor(ctx));
      if (type == 0x11) {
        streams.put(id, gossipEngine.server(upstream));
      } else {
        streams.put(id, handler.create(upstream));
      }
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(0x10);
      buffer.writeIntLE(id);
      ctx.write(buffer);
      flushFromWithinContextExecutor(ctx);
    } else {
      routeCommon(type, id, inBuffer, ctx);
    }
    inBuffer.release();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOGGER.error("channel-server-exception", cause);
    ctx.close();
  }
}
