/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.web.client.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.*;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.pool.PoolActions;
import org.adamalang.web.client.WebClientBaseMetrics;

import java.util.concurrent.TimeUnit;

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
  public void create(WebEndpoint request, Callback<WebClientSharedConnection> createdRaw) {
    Callback<WebClientSharedConnection> created = metrics.web_create_shared.wrap(createdRaw);
    WebClientSharedConnection connection = new WebClientSharedConnection(metrics, request, group);
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS));
        ch.pipeline().addLast(new WriteTimeoutHandler(240, TimeUnit.SECONDS));
        if (request.secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), request.host, request.port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(
          new SimpleChannelInboundHandler<HttpObject>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
              connection.handle(msg);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              connection.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_EXECUTE_INACTIVE));
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
              if (cause instanceof ReadTimeoutException || cause instanceof WriteTimeoutException) {
                connection.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_EXECUTE_TIMEOUT));
              } else {
                connection.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_EXECUTE_FAILED_EXCEPTION_CAUGHT, cause, EXLOGGER));
              }
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
