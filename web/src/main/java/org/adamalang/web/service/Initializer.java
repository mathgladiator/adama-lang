/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Initializer extends ChannelInitializer<SocketChannel> {
  private final Logger logger;
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final ServiceBase base;
  private final SslContext context;

  public Initializer(final WebConfig webConfig, final WebMetrics metrics, final ServiceBase base, SslContext context) {
    this.logger = LoggerFactory.getLogger("Initializer");
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    this.context = context;
  }

  @Override
  public void initChannel(final SocketChannel ch) throws Exception {
    logger.info("initializing channel: {}", ch.remoteAddress());
    final var pipeline = ch.pipeline();
    if (context != null) {
      pipeline.addLast("ssl", context.newHandler(ch.alloc()));
    }
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new ReadTimeoutHandler(webConfig.readTimeoutSeconds));
    pipeline.addLast(new WriteTimeoutHandler(webConfig.writeTimeoutSeconds));
    pipeline.addLast(new IdleStateHandler(webConfig.idleReadSeconds, webConfig.idleWriteSeconds, webConfig.idleAllSeconds, TimeUnit.SECONDS));
    pipeline.addLast(new HttpObjectAggregator(webConfig.maxContentLengthSize));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler("/~s", null, true, webConfig.maxWebSocketFrameSize, false, true, webConfig.timeoutWebsocketHandshake));
    pipeline.addLast(new WebHandler(webConfig, metrics, base.http(), base.downloader()));
    pipeline.addLast(new WebSocketHandler(webConfig, metrics, base));
  }
}
