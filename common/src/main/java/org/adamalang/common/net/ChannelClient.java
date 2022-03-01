package org.adamalang.common.net;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.HashMap;
import java.util.function.Consumer;

/** a single connection */
public class ChannelClient extends ChannelCommon {
  private final Lifecycle lifecycle;
  private HashMap<Integer, Consumer<Boolean>> initiations;
  private ChannelHandlerContext context;
  private ScheduledFuture<?> scheduledFlush;
  private boolean sentConnected;

  public ChannelClient(Lifecycle lifecycle) {
    super(1);
    this.lifecycle = lifecycle;
    this.initiations = new HashMap<>();
    this.scheduledFlush = null;
    this.sentConnected = false;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.context = ctx;
    lifecycle.connected(this);
    this.sentConnected = true;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    for (Consumer<Boolean> initiation : initiations.values()) {
      initiation.accept(false);
    }
    initiations.clear();
    lifecycle.disconnected();
    if (!sentConnected) {
      lifecycle.failed(new ErrorCodeException(ErrorCodes.NET_CONNECT_FAILED_TO_CONNECT_LONG));
    }
  }

  public void open(ByteStream downstream, Callback<ByteStream> opened) {
    context.executor().execute(() -> {
      int id = makeId();
      streams.put(id, downstream);
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(0x10);
      buffer.writeIntLE(id);
      initiations.put(id, (success) -> {
        if (success) {
          opened.success(new Remote(streams, id, context, () -> { flushFromWithinContextExecutor(context); }));
        } else {
          opened.failure(new ErrorCodeException(ErrorCodes.NET_FAILED_INITIATION));
        }
      });
      context.write(buffer);
      flushFromWithinContextExecutor(context);
    });
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf inBuffer = (ByteBuf) msg;
    byte type = inBuffer.readByte();
    int id = inBuffer.readIntLE();

    if (type == 0x10) {
      Consumer<Boolean> callback = initiations.remove(id);
      if (callback != null) {
        callback.accept(true);
      }
    } else {
      routeCommon(type, id, inBuffer, ctx);
    }
    inBuffer.release();
  }
}
