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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.nio.charset.StandardCharsets;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static void sendWithKeepAlive(final Config config, final ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
    final var responseStatus = res.status();
    String origin = req.headers().get("origin");
    if (origin != null) {
      res.headers().set("Access-Control-Allow-Origin", origin);
    }
    final var keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
    HttpUtil.setKeepAlive(res, keepAlive);
    final var future = ctx.writeAndFlush(res);
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  private final Config config;

  public WebHandler(Config config) {
    this.config = config;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    // analyze the request
    boolean isHealthCheck = config.healthCheckPath.equals(req.uri());

    // send the default response for bad or health checks
    HttpResponseStatus status = isHealthCheck ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST;
    final var content = (isHealthCheck ? "HEALTHY:" + System.currentTimeMillis() : "Bad Request").getBytes(StandardCharsets.UTF_8);
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), status, Unpooled.wrappedBuffer(content));
    HttpUtil.setContentLength(res, content.length);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
    sendWithKeepAlive(config, ctx, req, res);
  }
}
