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
package org.adamalang.web.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
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
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.web.assets.cache.WebHandlerAssetCache;
import org.adamalang.web.assets.transforms.TransformQueue;
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
  private final DomainFinder domainFinder;
  private final TransformQueue transformQueue;

  public Initializer(final WebConfig webConfig, final WebMetrics metrics, final ServiceBase base, final CertificateFinder certificateFinder, SslContext context, WebHandlerAssetCache cache, DomainFinder domainFinder, TransformQueue transformQueue) {
    this.logger = LoggerFactory.getLogger("Initializer");
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    this.certificateFinder = certificateFinder;
    this.context = context;
    this.cache = cache;
    this.domainFinder = domainFinder;
    this.transformQueue = transformQueue;
  }

  @Override
  public void initChannel(final SocketChannel ch) throws Exception {
    logger.info("initializing channel: {}", ch.remoteAddress());
    final var pipeline = ch.pipeline();
    pipeline.addLast(new ReadTimeoutHandler(webConfig.idleReadSeconds, TimeUnit.SECONDS));
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
    pipeline.addLast(new HttpObjectAggregator(webConfig.maxContentLengthSize));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler("/~s", null, true, webConfig.maxWebSocketFrameSize, false, true, webConfig.timeoutWebsocketHandshake));
    pipeline.addLast(new HttpContentCompressor());
    pipeline.addLast(new WebHandler(webConfig, metrics, base.http(), base.assets(), cache, domainFinder, transformQueue));
    pipeline.addLast(new WebSocketHandler(webConfig, metrics, base));
  }
}
