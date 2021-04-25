/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class Initializer extends ChannelInitializer<SocketChannel> {
  private final Nexus nexus;

  public Initializer(final Nexus nexus) {
    this.nexus = nexus;
  }

  @Override
  public void initChannel(final SocketChannel ch) throws Exception {
    final var pipeline = ch.pipeline();
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new HttpObjectAggregator(nexus.config.maxContentLengthSize));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler(nexus.config.websocketPath, null, true, nexus.config.maxWebSocketFrameSize, false, true, nexus.config.timeoutWebsocketHandshake));
    pipeline.addLast(new WebHandler(nexus));
    pipeline.addLast(new WebSocketHandler(nexus));
  }
}
