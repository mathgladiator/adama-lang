/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
  private final ServerNexus nexus;

  public ServerChannelInitializer(final ServerNexus nexus) {
    this.nexus = nexus;
  }

  @Override
  public void initChannel(final SocketChannel ch) throws Exception {
    final var pipeline = ch.pipeline();
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new HttpObjectAggregator(nexus.options.maxContentLengthSize()));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler(nexus.options.websocketPath(), null, true, nexus.options.maxWebSocketFrameSize(), false, true, nexus.options.timeoutWebsocketHandshake()));
    pipeline.addLast(new WebHandler(nexus));
    pipeline.addLast(new WebSocketHandler(nexus));
  }
}
