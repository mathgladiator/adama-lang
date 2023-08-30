/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.pool.PoolActions;
import org.adamalang.web.client.WebClientBaseMetrics;

/** how the shared connection pool is acted upon */
public class WebClientSharedConnectionActions implements PoolActions<WebEndpoint, WebClientSharedConnection> {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(WebClientSharedConnectionActions.class);
  private final EventLoopGroup group;
  private final WebClientBaseMetrics metrics;

  public WebClientSharedConnectionActions(WebClientBaseMetrics metrics, EventLoopGroup group) {
    this.metrics = metrics;
    this.group = group;
  }

  @Override
  public void create(WebEndpoint request, Callback<WebClientSharedConnection> created) {
    WebClientSharedConnection connection = new WebClientSharedConnection(metrics, request, group);
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (request.secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), request.host, request.port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new WriteTimeoutHandler(60));
        ch.pipeline().addLast(new ReadTimeoutHandler(10 * 60));
        ch.pipeline().addLast(
            new SimpleChannelInboundHandler<HttpObject>() {
              @Override
              protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                connection.handle(msg);
              }

              @Override
              public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                connection.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_EXECUTE_FAILED_EXCEPTION_CAUGHT, cause, EXLOGGER));
                ctx.close();
              }
            });
      }
    });

    b.connect(request.host, request.port).addListeners((ChannelFutureListener) future -> {
      if (future.isSuccess()) {
        connection.setChannel(future.channel());
        created.success(connection);
      } else {
        created.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_EXECUTE_FAILED_CONNECT, "Failed to connect[" + request.host + ":" + request.port + "]"));
      }
    });
  }

  @Override
  public void destroy(WebClientSharedConnection item) {
    item.close();
  }
}
