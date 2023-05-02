/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.WellKnownHandler;

import java.nio.charset.StandardCharsets;

public class RedirectHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static final byte[] EMPTY_RESPONSE = new byte[0];
  private final WebConfig webConfig;
  private final WellKnownHandler wellKnownHandler;

  public RedirectHandler(WebConfig webConfig, WellKnownHandler wellKnownHandler) {
    this.webConfig = webConfig;
    this.wellKnownHandler = wellKnownHandler;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    if (webConfig.healthCheckPath.equals(req.uri())) { // health checks
      sendImmediate(req, ctx, HttpResponseStatus.OK, ("HEALTHY:" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8), "text/plain; charset=UTF-8", true);
      return;
    }
    if (req.uri().equals("/.are.you.adama")) {
      sendImmediate(req, ctx, HttpResponseStatus.OK, ("YES").getBytes(StandardCharsets.UTF_8), "text/plain; charset=UTF-8", true);
      return;
    }
    if (req.uri().startsWith("/.well-known/")) {
      wellKnownHandler.handle(req.uri(), new Callback<String>() {
        @Override
        public void success(String response) {
          sendImmediate(req, ctx, HttpResponseStatus.OK, (response).getBytes(StandardCharsets.UTF_8), "text/plain", true);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          sendImmediate(req, ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, EMPTY_RESPONSE, "text/plain", true);
        }
      });
      return;
    }
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.PERMANENT_REDIRECT, Unpooled.wrappedBuffer(EMPTY_RESPONSE));
    res.headers().set(HttpHeaderNames.LOCATION, "https://" + req.headers().get(HttpHeaderNames.HOST) + req.uri());
    final var responseStatus = res.status();
    final var keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
    HttpUtil.setKeepAlive(res, keepAlive);
    final var future = ctx.writeAndFlush(res);
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  /** send an immediate data result */
  private void sendImmediate(FullHttpRequest req, final ChannelHandlerContext ctx, HttpResponseStatus status, byte[] content, String contentType, boolean cors) {
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), status, Unpooled.wrappedBuffer(content));
    HttpUtil.setContentLength(res, content.length);
    if (contentType != null) {
      res.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }
    final var responseStatus = res.status();
    final var keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
    HttpUtil.setKeepAlive(res, keepAlive);
    final var future = ctx.writeAndFlush(res);
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }
}
