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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(WebSocketHandler.class);
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final ServiceBase base;
  private boolean ended;
  private ServiceConnection connection;
  private ScheduledFuture<?> future;
  private long created;
  private AtomicLong latency;
  private boolean closed;

  public WebSocketHandler(final WebConfig webConfig, WebMetrics metrics, final ServiceBase base) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    this.connection = null;
    this.ended = false;
    this.future = null;
    this.created = System.currentTimeMillis();
    this.latency = new AtomicLong();
    this.closed = false;
    metrics.websockets_active.up();
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) {
    end(ctx);
  }

  private void end(ChannelHandlerContext ctx) {
    final var connToKill = clean();
    if (connToKill != null) {
      metrics.websockets_active_child_connections.down();
      connToKill.kill();
    }
    if (!closed) {
      closed = true;
      metrics.websockets_active.down();
      ctx.close();
    }
  }

  private ServiceConnection clean() {
    if (ended) {
      return null;
    }
    ended = true;
    if (future != null) {
      future.cancel(false);
      future = null;
    }
    if (connection != null) {
      final var copy = connection;
      connection = null;
      return copy;
    }
    return null;
  }

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
      // tell client all is ok
      ctx.writeAndFlush(new TextWebSocketFrame("{\"status\":\"connected\"}"));

      HttpHeaders headers = ((WebSocketServerProtocolHandler.HandshakeComplete) evt).requestHeaders();
      String origin = headers.get("Origin");
      String ip = ctx.channel().remoteAddress().toString();
      String userAgent = headers.get("User-Agent");
      ConnectionContext context = new ConnectionContext(origin, ip, userAgent);

      // establish the service
      connection = base.establish(context);
      metrics.websockets_active_child_connections.up();

      // start the heartbeat loop
      Runnable heartbeatLoop =
          () -> {
            if (connection != null && !connection.keepalive()) {
              metrics.websockets_heartbeat_failure.run();
              ctx.writeAndFlush(
                  new TextWebSocketFrame(
                      "{\"status\":\"disconnected\",\"reason\":\"keepalive-failure\"}"));
              end(ctx);
            } else {
              metrics.websockets_send_heartbeat.run();
              ctx.writeAndFlush(
                  new TextWebSocketFrame(
                      "{\"ping\":"
                          + (System.currentTimeMillis() - created)
                          + ",\"latency\":\""
                          + latency.get()
                          + "\"}"));
            }
          };

      // schedule the heartbeat loop
      future =
          ctx.executor()
              .scheduleAtFixedRate(
                  heartbeatLoop,
                  webConfig.heartbeatTimeMilliseconds,
                  webConfig.heartbeatTimeMilliseconds,
                  TimeUnit.MILLISECONDS);
    }
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame)
      throws Exception {
    try {
      if (!(frame instanceof TextWebSocketFrame)) {
        throw new ErrorCodeException(ErrorCodes.ONLY_ACCEPTS_TEXT_FRAMES);
      }
      // parse the request
      final var requestNode = Json.parseJsonObject(((TextWebSocketFrame) frame).text());
      if (requestNode.has("pong")) {
        latency.set(System.currentTimeMillis() - created - requestNode.get("ping").asLong());
        return;
      }

      JsonRequest request = new JsonRequest(requestNode);
      final var id = request.id();

      // tie a responder to the request
      final JsonResponder responder =
          new JsonResponder() {
            @Override
            public void stream(String json) {
              ctx.writeAndFlush(
                  new TextWebSocketFrame(
                      "{\"deliver\":" + id + ",\"done\":false,\"response\":" + json + "}"));
            }

            @Override
            public void finish(String json) {
              ctx.writeAndFlush(
                  new TextWebSocketFrame(
                      "{\"deliver\":" + id + ",\"done\":true,\"response\":" + json + "}"));
            }

            @Override
            public void error(ErrorCodeException ex) {
              ctx.writeAndFlush(
                  new TextWebSocketFrame("{\"failure\":" + id + ",\"reason\":" + ex.code + "}"));
            }
          };

      // execute the request
      connection.execute(request, responder);

    } catch (Exception ex) {
      ErrorCodeException codedException = ErrorCodeException.detectOrWrap(ErrorCodes.UNCAUGHT_EXCEPTION_WEB_SOCKET, ex, LOGGER);
      ctx.writeAndFlush(
          new TextWebSocketFrame(
              "{\"status\":\"disconnected\",\"reason\":" + codedException.code + "}"));
      end(ctx);
    }
  }
}
