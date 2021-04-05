/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.adamalang.netty.ErrorCodes;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.contracts.AuthCallback;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
  private boolean ended;
  private final ServerNexus nexus;
  private AdamaSession session;
  private boolean alive;
  private ScheduledFuture<?> future;
  private long created;
  private AtomicLong latency;

  public WebSocketHandler(final ServerNexus nexus) {
    this.nexus = nexus;
    ended = false;
    this.alive = true;
    this.future = null;
    this.created = System.currentTimeMillis();
    this.latency = new AtomicLong();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) {
    heartbeatEnd();
  }

  private synchronized void heartbeatEnd() {
    alive = false;
    if (future != null) {
      future.cancel(false);
      future = null;
    }
  }

  private synchronized void heartbeatStart(ScheduledFuture<?> future) {
    if (alive) {
      this.future = future;
    } else {
      future.cancel(false);
      this.future = null;
    }
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) throws Exception {
    if (frame instanceof TextWebSocketFrame) {
      final var request = Utility.parseJsonObject(((TextWebSocketFrame) frame).text());
      if (request.has("pong")) {
        latency.set(System.currentTimeMillis() - created - request.get("ping").asLong());
        return;
      }
      // extract and validate the ID
      final var idNode = request.get("id");
      if (idNode == null || idNode.isNull() || !(idNode.isTextual() || idNode.isIntegralNumber())) {
        ctx.channel().close();
        end();
        return;
      }
      final var id = idNode.asInt();
      // the id allows us to respond, create the responder
      final JsonResponder responder = new JsonResponder() {
        @Override
        public void failure(ErrorCodeException ex) {
          final var setup = Utility.createObjectNode();
          setup.put("failure", id);
          setup.put("reason", ex.code);
          ctx.writeAndFlush(new TextWebSocketFrame(setup.toString()));
        }

        @Override
        public void respond(final ObjectNode response, final boolean done, final HashMap<String, String> httpHeaders) {
          final var frame = Utility.createObjectNode();
          frame.put("deliver", id);
          frame.put("done", done);
          frame.set("response", response);
          ctx.writeAndFlush(new TextWebSocketFrame(frame.toString()));
        }
      };

      try {
        nexus.handler.handle(session(), request, responder);
      } catch (final ErrorCodeException ex) {
        responder.failure(ex);
      } catch (final Throwable ex) {
        responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E5_UNCAUGHT_EXCEPTION_WEB_SOCKET, ex));
        ctx.channel().close();
        end();
      }
    } else {
      ctx.channel().close();
      end();
    }
  }

  private void end() {
    final var sessionToRemove = endWithLock();
    if (sessionToRemove != null) {
      sessionToRemove.kill();
    }
  }

  private synchronized AdamaSession endWithLock() {
    if (ended) { return null; }
    ended = true;
    if (session != null) {
      final var copy = session;
      session = null;
      return copy;
    }
    return null;
  }

  private synchronized void initWithLock(final AdamaSession session) {
    if (ended) {
      session.kill();
      return;
    }
    this.session = session;
  }

  private synchronized AdamaSession session() {
    return session;
  }

  private static String getAuthToken(WebSocketServerProtocolHandler.HandshakeComplete complete) {
    final var cookieHeader = complete.requestHeaders().get(HttpHeaderNames.COOKIE);
    QueryStringDecoder qsd = new QueryStringDecoder(complete.requestUri());
    List<String> tokens = qsd.parameters().get(AdamaCookieCodec.ADAMA_AUTH_COOKIE_QUERY_STRING_OVERRIDE_NAME);
    if (tokens != null && tokens.size() == 1) {
      return tokens.get(0);
    }
    return AdamaCookieCodec.extractCookieValue(cookieHeader, AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME);
  }

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
    try {
      if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
        final var complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
        final AuthCallback authCallback = new AuthCallback() {
          @Override
          public void failure() {
            final var setup = Utility.createObjectNode();
            setup.put("signal", "setup");
            setup.put("status", "failed_auth");
            ctx.writeAndFlush(new TextWebSocketFrame(setup.toString()));
          }

          @Override
          public void success(final AdamaSession incomingSession) {
            initWithLock(incomingSession);
            final var setup = Utility.createObjectNode();
            setup.put("signal", "setup");
            setup.put("status", "connected");
            ctx.writeAndFlush(new TextWebSocketFrame(setup.toString()));
            Runnable heartbeatLoop = () -> {
              final var ping = Utility.createObjectNode();
              ping.put("ping", (System.currentTimeMillis() - created));
              ping.put("latency", latency.get());
              ctx.writeAndFlush(new TextWebSocketFrame(ping.toString()));
            };
            heartbeatStart(nexus.heartbeat.scheduleAtFixedRate(heartbeatLoop, 1000, 1000, TimeUnit.MILLISECONDS));
          }
        };
        final var authToken = getAuthToken(complete);
        if (authToken != null) {
          nexus.authenticator.authenticate(authToken, authCallback);
        } else {
          final var setup = Utility.createObjectNode();
          setup.put("signal", "setup");
          setup.put("status", "failed_setup_no_cookie");
          ctx.writeAndFlush(new TextWebSocketFrame(setup.toString()));
        }
      }
    } catch (final Exception ex) {
      end();
      ctx.close();
    }
  }
}
