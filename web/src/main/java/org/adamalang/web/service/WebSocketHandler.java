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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.common.Json;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame>  {
  private boolean ended;
  private final WebConfig webConfig;
  private final ServiceBase base;
  private ServiceConnection connection;
  private ScheduledFuture<?> future;
  private long created;
  private AtomicLong latency;

  public WebSocketHandler(final WebConfig webConfig, final ServiceBase base) {
    this.webConfig = webConfig;
    this.base = base;
    this.connection = null;
    this.ended = false;
    this.future = null;
    this.created = System.currentTimeMillis();
    this.latency = new AtomicLong();
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) {
    end(ctx);
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

        System.err.println("REQ:" + requestNode.toString());
        JsonRequest request = new JsonRequest(requestNode);
        final var id = request.id();

        // tie a responder to the request
        final JsonResponder responder = new JsonResponder() {
          @Override
          public void stream(String json) {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"deliver\":" + id + ",\"done\":false,\"response\":"+ json + "}"));
          }

          @Override
          public void finish(String json) {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"deliver\":" + id + ",\"done\":true,\"response\":"+ json + "}"));
          }

          @Override
          public void error(ErrorCodeException ex) {
            ctx.writeAndFlush(new TextWebSocketFrame("{\"failure\":" + id + ",\"reason\":" + ex.code + "}"));
          }
        };

        // execute the request
        connection.execute(request, responder);

      } catch (Exception ex) {
        ErrorCodeException codedException = ErrorCodeException.detectOrWrap(ErrorCodes.UNCAUGHT_EXCEPTION_WEB_SOCKET, ex);
        ctx.writeAndFlush(new TextWebSocketFrame("{\"status\":\"disconnected\",\"reason\":"+codedException.code+"}"));
        end(ctx);
      }
  }

  private void end(ChannelHandlerContext ctx) {
    final var connToKill = clean();
    if (connToKill != null) {
      connToKill.kill();
    }
    ctx.channel().close();
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

      // start the heartbeat loop
      Runnable heartbeatLoop = () -> {
        if (connection != null && !connection.keepalive()) {
          ctx.writeAndFlush(new TextWebSocketFrame("{\"status\":\"disconnected\",\"reason\":\"keepalive-failure\"}"));
          end(ctx);
        } else {
          ctx.writeAndFlush(new TextWebSocketFrame("{\"ping\":"+(System.currentTimeMillis() - created)+",\"latency\":\""+latency.get()+"\"}"));
        }
      };

      // schedule the heartbeat loop
      future = ctx.executor().scheduleAtFixedRate(heartbeatLoop, webConfig.heartbeatTimeMilliseconds, webConfig.heartbeatTimeMilliseconds, TimeUnit.MILLISECONDS);
    }
  }

}
