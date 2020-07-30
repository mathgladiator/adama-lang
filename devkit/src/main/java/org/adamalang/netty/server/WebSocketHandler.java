/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.util.HashMap;
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
  private ChannelHandlerContext context;
  private boolean ended;
  private final ServerNexus nexus;
  private AdamaSession session;

  public WebSocketHandler(final ServerNexus nexus) {
    this.nexus = nexus;
    ended = false;
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) {
    end();
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) throws Exception {
    if (frame instanceof TextWebSocketFrame) {
      final var request = Utility.parseJsonObject(((TextWebSocketFrame) frame).text());
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
        public void failure(final int reason, final Exception e) {
          final var setup = Utility.createObjectNode();
          setup.put("failure", id);
          setup.put("reason", reason);
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
      } catch (final ErrorCodeException ece) {
        responder.failure(ece.code, ece);
      } catch (final Exception ex) {
        responder.failure(ErrorCodeException.SERVICE_UNKNOWN_FAILURE, ex);
        ctx.channel().close();
        end();
      }
    } else {
      throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
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

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
    try {
      if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
        context = ctx;
        final var complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
        final var cookieHeader = complete.requestHeaders().get(HttpHeaderNames.COOKIE);
        new QueryStringDecoder(complete.requestUri());
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
          }
        };
        final var authToken = AdamaCookieCodec.extractCookieValue(cookieHeader, AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME);
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
