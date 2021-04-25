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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.adamalang.api.auth.AuthenticatorCallback;
import org.adamalang.api.commands.Request;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.session.Session;
import org.adamalang.api.util.Json;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame>  {
  private boolean ended;
  private final Nexus nexus;
  private Session session;
  private ScheduledFuture<?> future;
  private long created;
  private AtomicLong latency;

  public WebSocketHandler(final Nexus nexus) {
    this.nexus = nexus;
    this.session = null;
    this.ended = false;
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
    end();
  }

  private synchronized void initWithLock(final Session session, ScheduledFuture<?> future) {
    if (ended) {
      session.kill();
      future.cancel(false);
      this.future = null;
      return;
    }
    this.session = session;
    this.future = future;
  }

  private synchronized Session endWithLock() {
    if (ended) { return null; }
    ended = true;
    if (future != null) {
      future.cancel(false);
      future = null;
    }
    if (session != null) {
      final var copy = session;
      session = null;
      return copy;
    }
    return null;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) throws Exception {
    CommandResponder responderExtern = null;
      try {
        if (!(frame instanceof TextWebSocketFrame)) {
          throw new Exception("only accepts text frames");
        }
        final var requestNode = Json.parseJsonObject(((TextWebSocketFrame) frame).text());
        if (requestNode.has("pong")) {
          latency.set(System.currentTimeMillis() - created - requestNode.get("ping").asLong());
          return;
        }

        Request request = new Request(requestNode);
        final var id = request.id();

        final CommandResponder responder = new CommandResponder() {
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
        responderExtern = responder;

        if (session() == null) {
          responder.error(new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_HAS_NO_SESSION));
          return;
        }

        NtClient impersonate = request.impersonate();
        if (impersonate != null) {
          nexus.authenticatorService.authenticateImpersonation(session(), impersonate, new AuthenticatorCallback() {
            @Override
            public void success(String token, Session session) {
              nexus.service.handle(session, request, responder);
            }

            @Override
            public void failure() {
              responder.error(new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_IMPERSONATION_FAILED));
            }
          });
        } else {
          nexus.service.handle(session(), request, responder);
        }
      } catch (Exception ex) {
        if (responderExtern != null) {
          responderExtern.error(new ErrorCodeException(ErrorCodes.E5_UNCAUGHT_EXCEPTION_WEB_SOCKET, ex));
        }
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

  private synchronized Session session() {
    return session;
  }

  private String extractAuthCookie(String cookieHeader) {
    String value = null;
    if (cookieHeader != null) {
      final var cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
      for (final Cookie cookie : cookies) {
        if (nexus.config.websocketAuthCookieName.equals(cookie.name())) {
          value = cookie.value();
        }
      }
    }
    return value;
  }

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
    try {
      if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
        final var complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
        final var cookieHeader = complete.requestHeaders().get(HttpHeaderNames.COOKIE);
        final var authToken = extractAuthCookie(cookieHeader);
        if (authToken != null) {
          nexus.authenticatorService.authenticateByToken(authToken, new AuthenticatorCallback() {
            @Override
            public void success(String _token, Session session) {
              Runnable heartbeatLoop = () -> {
                ctx.writeAndFlush(new TextWebSocketFrame("{\"ping\":"+(System.currentTimeMillis() - created)+",\"latency\":\""+latency.get()+"\"}"));
              };
              initWithLock(session, ctx.executor().scheduleAtFixedRate(heartbeatLoop, nexus.config.heartbeatTimeMilliseconds, nexus.config.heartbeatTimeMilliseconds, TimeUnit.MILLISECONDS));
              ctx.writeAndFlush(new TextWebSocketFrame("{\"signal\":\"setup\",\"status\":\"connected\",\"session_id\":\"" + UUID.randomUUID() + "\"}"));
            }

            @Override
            public void failure() {
              ctx.writeAndFlush(new TextWebSocketFrame("{\"signal\":\"setup\",\"status\":\"failed_auth\"}"));
            }
          });
        } else {
          ctx.writeAndFlush(new TextWebSocketFrame("{\"signal\":\"setup\",\"status\":\"failed_setup_no_cookie\"}"));
        }
      }
    } catch (final Exception ex) {
      end();
      ctx.close();
    }
  }

}
