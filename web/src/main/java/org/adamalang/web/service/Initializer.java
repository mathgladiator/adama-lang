/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.web.assets.cache.WebHandlerAssetCache;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.contracts.ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Initializer extends ChannelInitializer<SocketChannel> {
  private final Logger logger;
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final ServiceBase base;
  private final CertificateFinder certificateFinder;
  private final SslContext context;
  private final WebHandlerAssetCache cache;

  public Initializer(final WebConfig webConfig, final WebMetrics metrics, final ServiceBase base, final CertificateFinder certificateFinder, SslContext context, WebHandlerAssetCache cache) {
    this.logger = LoggerFactory.getLogger("Initializer");
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    this.certificateFinder = certificateFinder;
    this.context = context;
    this.cache = cache;
  }

  @Override
  public void initChannel(final SocketChannel ch) throws Exception {
    logger.info("initializing channel: {}", ch.remoteAddress());
    final var pipeline = ch.pipeline();
    if (context != null) {
      pipeline.addLast("sni", new SniHandler((domain, promise) -> {
        certificateFinder.fetch(domain, new Callback<SslContext>() {
          @Override
          public void success(SslContext contextToUse) {
            if (contextToUse != null) {
              promise.setSuccess(contextToUse);
            } else { // the default context when given a null
              promise.setSuccess(context);
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            promise.setFailure(ex);
          }
        });
        return promise;
      }));
    }
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new ReadTimeoutHandler(webConfig.readTimeoutSeconds));
    pipeline.addLast(new WriteTimeoutHandler(webConfig.writeTimeoutSeconds));
    pipeline.addLast(new IdleStateHandler(webConfig.idleReadSeconds, webConfig.idleWriteSeconds, webConfig.idleAllSeconds, TimeUnit.SECONDS));
    pipeline.addLast(new HttpObjectAggregator(webConfig.maxContentLengthSize));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler("/~s", null, true, webConfig.maxWebSocketFrameSize, false, true, webConfig.timeoutWebsocketHandshake));
    pipeline.addLast(new WebHandler(webConfig, metrics, base.http(), base.assets(), cache));
    pipeline.addLast(new WebSocketHandler(webConfig, metrics, base));
  }
}
