package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ChannelServer extends ChannelCommon {
  private final Handler handler;

  public ChannelServer(Handler handler) {
    super(2);
    this.handler = handler;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf inBuffer = (ByteBuf) msg;
    byte type = inBuffer.readByte();
    int id = inBuffer.readIntLE();
    if (type == 0x10) {
      ByteStream upstream = new Remote(streams, id, ctx, () -> flushFromWithinContextExecutor(ctx));
      streams.put(id, handler.create(upstream));
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
}
