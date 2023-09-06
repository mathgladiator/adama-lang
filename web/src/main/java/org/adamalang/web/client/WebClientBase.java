/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.pool.AsyncPool;
import org.adamalang.common.pool.PoolItem;
import org.adamalang.web.client.pool.WebClientSharedConnection;
import org.adamalang.web.client.pool.WebClientSharedConnectionActions;
import org.adamalang.web.client.pool.WebEndpoint;
import org.adamalang.web.client.socket.WebClientConnectionInboundHandler;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.WebConfig;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebClientBase {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(WebClientBase.class);
  private static final byte[] EMPTY_BODY = new byte[0];

  public final WebConfig config;
  private final EventLoopGroup group;
  private final AsyncPool<WebEndpoint, WebClientSharedConnection> pool;
  private final WebClientSharedConnectionActions actions;
  private final WebClientBaseMetrics metrics;
  private final SimpleExecutor executor;

  public WebClientBase(WebClientBaseMetrics metrics, WebConfig config) {
    this.metrics = metrics;
    this.executor = SimpleExecutor.create("web-client-base");
    this.config = config;
    group = new NioEventLoopGroup();
    this.actions = new WebClientSharedConnectionActions(metrics, group);
    this.pool = new AsyncPool<>(executor, TimeSource.REAL_TIME, //
        config.sharedConnectionPoolMaxLifetimeMilliseconds, //
        config.sharedConnectionPoolMaxUsageCount, //
        config.sharedConnectionPoolMaxPoolSize, //
        ErrorCodes.WEB_BASE_EXECUTE_SHARED_TOO_MANY_INFLIGHT, actions);
    pool.scheduleSweeping(new AtomicBoolean(true));
  }

  public void shutdown() {
    executor.shutdown();
    group.shutdownGracefully(50, 500, TimeUnit.MILLISECONDS);
  }

  public void executeShared(SimpleHttpRequest request, SimpleHttpResponder responder) {
    pool.get(new WebEndpoint(URI.create(request.url)), new Callback<>() {
      @Override
      public void success(PoolItem<WebClientSharedConnection> connection) {
        connection.item().writeRequest(request, new SimpleHttpResponder() {
          @Override
          public void start(SimpleHttpResponseHeader header) {
            responder.start(header);
          }

          @Override
          public void bodyStart(long size) {
            responder.bodyStart(size);
          }

          @Override
          public void bodyFragment(byte[] chunk, int offset, int len) {
            responder.bodyFragment(chunk, offset, len);
          }

          @Override
          public void bodyEnd() {
            try {
              responder.bodyEnd();
            } finally {
              connection.returnToPool();
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            try {
              responder.failure(ex);
            } finally {
              connection.signalFailure();
            }
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.failure(ex);
      }
    });
  }

  /** start of a new simpler execute http request */
  public void execute(SimpleHttpRequest request, SimpleHttpResponder responder) {
    URI uri = URI.create(request.url);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("https");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();
    String requestPath = uri.getRawPath() + (uri.getRawQuery() != null ? ("?" + uri.getRawQuery()) : "");
    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), host, port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new WriteTimeoutHandler(60));
        ch.pipeline().addLast(new ReadTimeoutHandler(10 * 60));
        ch.pipeline().addLast(
            new SimpleChannelInboundHandler<HttpObject>() {
              byte[] chunk = new byte[8196];
              @Override
              protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                if (msg instanceof HttpResponse) {
                  HttpResponse httpResponse = (HttpResponse) msg;
                  TreeMap<String, String> headers = new TreeMap<>();
                  for (Map.Entry<String, String> header : httpResponse.headers()) {
                    headers.put(header.getKey().toLowerCase(Locale.ENGLISH), header.getValue());
                  }
                  String contentLength = headers.get("content-length");
                  long size = -1;
                  if (contentLength != null) {
                    size = Long.parseLong(contentLength);
                  } else {
                    size = -1;
                  }
                  responder.start(new SimpleHttpResponseHeader(httpResponse.status().code(), headers));
                  responder.bodyStart(size);
                } else if (msg instanceof HttpContent) {
                  HttpContent content = (HttpContent) msg;
                  ByteBuf body = content.content();
                  while (body.readableBytes() > 0) {
                    int rd = Math.min(body.readableBytes(), chunk.length);
                    body.readBytes(chunk, 0, rd);
                    responder.bodyFragment(chunk, 0, rd);
                  }
                  if (msg instanceof LastHttpContent) {
                    responder.bodyEnd();
                    ctx.close();
                  }
                }
              }

              @Override
              public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.WEB_BASE_EXECUTE_FAILED_EXCEPTION_CAUGHT, cause, EXLOGGER));
                ctx.close();
              }
            });
      }
    });

    b.connect(host, port).addListeners((ChannelFutureListener) future -> {
      if (future.isSuccess()) {
        boolean success = false;
        try {
          // convert the method
          HttpMethod method = HttpMethod.valueOf(request.method.toUpperCase());
          // initialiize the headers
          HttpHeaders headers = new DefaultHttpHeaders(true);
          headers.set("Host", host);
          // get the body size
          long bodySize = request.body.size();
          if (method != HttpMethod.GET || bodySize > 0) {
            headers.set(HttpHeaderNames.CONTENT_LENGTH, bodySize);
          }
          // apply the headers
          for (Map.Entry<String, String> entry : request.headers.entrySet()) {
            headers.set(entry.getKey(), entry.getValue());
          }
          if (bodySize < 32 * 1024) {
            final ByteBuf content;
            if (bodySize == 0) {
              content = Unpooled.wrappedBuffer(EMPTY_BODY);
            } else {
              byte[] buffer = new byte[8196];
              content = Unpooled.buffer((int) bodySize);
              int left = (int) bodySize;
              while (left > 0) {
                int sz = request.body.read(buffer);
                content.writeBytes(buffer, 0, sz);
                left -= sz;
              }
            }
            future.channel().writeAndFlush(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, requestPath, content, headers, new DefaultHttpHeaders(true)));
          } else {
            future.channel().writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, requestPath, headers));
            long left = bodySize;
            while (left > 0) {
              byte[] buffer = new byte[8196];
              int sz = request.body.read(buffer);
              final ByteBuf content;
              if (sz == buffer.length) {
                content = Unpooled.wrappedBuffer(buffer);
              } else {
                content = Unpooled.wrappedBuffer(Arrays.copyOfRange(buffer, 0, sz));
              }
              left -= sz;
              if (left == 0) {
                future.channel().writeAndFlush(new DefaultLastHttpContent(content));
              } else {
                future.channel().writeAndFlush(new DefaultHttpContent(content));
              }
            }
          }
          success = true;
        } finally {
          request.body.finished(success);
        }
      } else {
        responder.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_EXECUTE_FAILED_CONNECT, "Failed to connect[" + host + ":" + port + "]"));
      }
    });
  }

  public void open(String endpoint, WebLifecycle lifecycle) {
    URI uri = URI.create(endpoint);
    String host = uri.getHost();
    boolean secure = uri.getScheme().equals("wss") || uri.getScheme().equals("https");
    int port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();

    final var b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(final SocketChannel ch) throws Exception {
        if (secure) {
          ch.pipeline().addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), host, port));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(2424242));
        ch.pipeline().addLast(new WriteTimeoutHandler(3));
        ch.pipeline().addLast(new ReadTimeoutHandler(3));
        ch.pipeline().addLast(WebSocketClientCompressionHandler.INSTANCE);
        ch.pipeline().addLast(new WebSocketClientProtocolHandler( //
            URI.create(endpoint), //
            WebSocketVersion.V13, //
            null, //
            true, //
            new DefaultHttpHeaders(), //
            50000));
        ch.pipeline().addLast(new WebClientConnectionInboundHandler(lifecycle));
      }
    });
    b.connect(host, port).addListeners((ChannelFutureListener) channelFuture -> {
      if (!channelFuture.isSuccess()) {
        lifecycle.failure(new Exception("Failed to connect to " + host + ":" + port));
        lifecycle.disconnected();
      }
    });
  }
}
