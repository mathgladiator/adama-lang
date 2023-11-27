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

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.adamalang.ErrorCodes;
import org.adamalang.ErrorTable;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.common.Platform;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
  private static final ConnectionContext DEFAULT_CONTEXT = new ConnectionContext("unknown", "unknown", "unknown", "assetKey", null);
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final ServiceBase base;
  private final long created;
  private final AtomicLong latency;
  private ServiceConnection connection;
  private ScheduledFuture<?> future;
  private boolean closed;
  private ConnectionContext context;

  public WebSocketHandler(final WebConfig webConfig, WebMetrics metrics, final ServiceBase base) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.base = base;
    this.connection = null;
    this.future = null;
    this.created = System.currentTimeMillis();
    this.latency = new AtomicLong();
    this.closed = false;
    this.context = DEFAULT_CONTEXT;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    metrics.websockets_active.up();
    metrics.websockets_start.run();
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    metrics.websockets_active.down();
    metrics.websockets_end.run();
    end(ctx);
    super.channelInactive(ctx);
  }

  private synchronized void end(ChannelHandlerContext ctx) {
    try {
      if (closed) {
        return;
      }
      closed = true;
      if (future != null) {
        future.cancel(false);
        future = null;
      }
      if (connection != null) {
        metrics.websockets_active_child_connections.down();
        connection.kill();
        connection = null;
      }
      ctx.close();
    } catch (Exception ex) {
      LOG.error("end-exception", ex);
      metrics.websockets_end_exception.run();
    }
  }

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete && !closed) {
      HttpHeaders headers = ((WebSocketServerProtocolHandler.HandshakeComplete) evt).requestHeaders();
      context = ConnectionContextFactory.of(ctx, headers);
      // tell client all is ok
      ObjectNode connected = Json.newJsonObject();
      connected.put("status", "connected");
      connected.put("version", Platform.VERSION);
      connected.put("assets", context.assetKey != null);
      if (context.identities != null) {
        ObjectNode idents = connected.putObject("identities");
        for (String ident : context.identities.keySet()) {
          idents.put(ident, true);
        }
      }
      ctx.writeAndFlush(new TextWebSocketFrame(connected.toString()));
      // establish the service
      connection = base.establish(context);
      metrics.websockets_active_child_connections.up();
      // start the heartbeat loop
      Runnable heartbeatLoop = () -> {
        if (connection != null && !connection.keepalive()) {
          metrics.websockets_heartbeat_failure.run();
          ctx.writeAndFlush(new TextWebSocketFrame("{\"status\":\"disconnected\",\"reason\":\"keepalive-failure\"}"));
          end(ctx);
        } else {
          metrics.websockets_send_heartbeat.run();
          ctx.writeAndFlush(new TextWebSocketFrame("{\"ping\":" + (System.currentTimeMillis() - created) + ",\"latency\":\"" + latency.get() + "\"}"));
        }
      };

      // schedule the heartbeat loop
      future = ctx.executor().scheduleAtFixedRate(heartbeatLoop, webConfig.heartbeatTimeMilliseconds, webConfig.heartbeatTimeMilliseconds, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    try {
      super.exceptionCaught(ctx, cause);
      if (cause instanceof IOException && "Connection timed out".equals(cause.getMessage())) {
        metrics.websockets_timed_out.run();
      } else if (cause instanceof SocketException) {
        metrics.websockets_socket_exception.run();
      } else if (cause instanceof DecoderException) {
        metrics.websockets_decode_exception.run();
      } else {
        metrics.websockets_uncaught_exception.run();
        LOG.error("exception", cause);
      }
    } finally {
      end(ctx);
    }
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) throws Exception {
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
      JsonRequest request = new JsonRequest(requestNode, context);
      final var id = request.id();
      // tie a responder to the request
      final JsonResponder responder = new JsonResponder() {
        @Override
        public void stream(String json) {
          ctx.writeAndFlush(new TextWebSocketFrame("{\"deliver\":" + id + ",\"done\":false,\"response\":" + json + "}"));
        }

        @Override
        public void finish(String json) {
          if (json == null) {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"deliver\":" + id + ",\"done\":true}"));
          } else {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"deliver\":" + id + ",\"done\":true,\"response\":" + json + "}"));
          }
        }

        @Override
        public void error(ErrorCodeException ex) {
          boolean retry = ErrorTable.INSTANCE.shouldRetry(ex.code);
          ctx.writeAndFlush(new TextWebSocketFrame("{\"failure\":" + id + ",\"reason\":" + ex.code + ",\"retry\":" + (retry ? "true" : "false") + "}"));
        }
      };
      // execute the request
      connection.execute(request, responder);
    } catch (Exception ex) {
      ErrorCodeException codedException = ErrorCodeException.detectOrWrap(ErrorCodes.UNCAUGHT_EXCEPTION_WEB_SOCKET, ex, LOGGER);
      ctx.writeAndFlush(new TextWebSocketFrame("{\"status\":\"disconnected\",\"reason\":" + codedException.code + "}"));
      end(ctx);
    }
  }
}
