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
    if (req.uri().startsWith("/.well-known/") && !WellKnownHandler.DONT_HANDLE(req.uri())) {
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
