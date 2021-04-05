/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.adamalang.netty.ErrorCodes;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.contracts.AuthCallback;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.netty.contracts.StaticSite;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static void sendWithKeepAlive(final ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
    final var responseStatus = res.status();
    String host = req.headers().get("host");
    // if localhost, then... enable all of them
    if (host != null && (host.startsWith("localhost") || host.startsWith("127.0.0.1"))) {
      String origin = req.headers().get("origin");
      if (origin != null) {
        res.headers().set("Access-Control-Allow-Origin", origin);
        res.headers().set("Access-Control-Allow-Credentials", true);
      }
    }
    final var keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
    HttpUtil.setKeepAlive(res, keepAlive);
    final var future = ctx.writeAndFlush(res);
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  public final ServerNexus nexus;

  public WebHandler(final ServerNexus nexus) {
    this.nexus = nexus;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    var generatedFailureStatusCode = HttpResponseStatus.BAD_REQUEST;
    FullHttpResponse response = null;
    if (req.decoderResult().isSuccess()) {
      try {
        final var qsd = new QueryStringDecoder(req.uri());
        if (qsd.path().equals(nexus.options.healthCheckPath())) {
          sendWithKeepAlive(ctx, req, StaticSite.ofHTML(req, "YES:" + System.currentTimeMillis()));
          return;
        }
        if (req.method() == HttpMethod.POST) {
          final var request = Utility.parseJsonObject(req.content().toString(StandardCharsets.UTF_8));
          final AuthCallback afterAuth = new AuthCallback() {
            @Override
            public void failure() {
              success(null);
            }

            @Override
            public void success(final AdamaSession session) {
              final JsonResponder responder = new JsonResponder() {
                @Override
                public void failure(ErrorCodeException ex) {
                  final var content = ("{\"error\":" + ex.code + "}").getBytes(StandardCharsets.UTF_8);
                  final FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(content));
                  HttpUtil.setContentLength(response, content.length);
                  sendWithKeepAlive(ctx, req, response);
                }

                @Override
                public void respond(final ObjectNode node, final boolean done, final HashMap<String, String> headers) {
                  final var content = node.toString().getBytes(StandardCharsets.UTF_8);
                  final FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.copiedBuffer(content));
                  if (headers != null) {
                    for (final Map.Entry<String, String> headerEntry : headers.entrySet()) {
                      response.headers().set(headerEntry.getKey(), headerEntry.getValue());
                    }
                  }
                  HttpUtil.setContentLength(response, content.length);
                  sendWithKeepAlive(ctx, req, response);
                }
              };
              try {
                nexus.handler.handle(session, request, responder);
              } catch (final ErrorCodeException ex) {
                responder.failure(ex);
              } catch (final Throwable ex) {
                responder.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E5_UNCAUGHT_EXCEPTION_WEB_HANDLER, ex));
              }
              if (session != null) {
                session.kill();
              }
            }
          };
          final var authToken = AdamaCookieCodec.extractCookieValue(req.headers().get(HttpHeaderNames.COOKIE), AdamaCookieCodec.ADAMA_AUTH_COOKIE_NAME);
          if (authToken != null) {
            nexus.authenticator.authenticate(authToken, afterAuth);
          } else {
            afterAuth.failure();
          }
          return;
        }
        if (response == null && req.method() == HttpMethod.GET) {
          response = nexus.site.request(qsd.path().substring(1), HttpResponseStatus.OK, req);
          if (response == null) {
            generatedFailureStatusCode = HttpResponseStatus.NOT_FOUND;
            response = nexus.site.request("errors.404.html", HttpResponseStatus.NOT_FOUND, req);
          }
        }
      } catch (final Exception e) {
        e.printStackTrace();
        generatedFailureStatusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        response = nexus.site.request("errors.500.html", HttpResponseStatus.INTERNAL_SERVER_ERROR, req);
      }
    }
    if (response != null) {
      sendWithKeepAlive(ctx, req, response);
    } else {
      final var content = ("Error:" + generatedFailureStatusCode.toString()).getBytes();
      response = new DefaultFullHttpResponse(req.protocolVersion(), generatedFailureStatusCode, Unpooled.copiedBuffer(content));
      HttpUtil.setContentLength(response, content.length);
      sendWithKeepAlive(ctx, req, response);
    }
  }
}
