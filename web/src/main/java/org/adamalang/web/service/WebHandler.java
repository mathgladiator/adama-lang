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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.adamalang.web.contracts.HtmlHandler;

import java.nio.charset.StandardCharsets;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private final WebConfig webConfig;
  private final HtmlHandler html;

  public WebHandler(WebConfig webConfig, HtmlHandler html) {
    this.webConfig = webConfig;
    this.html = html;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    String htmlResult = html.handle(req.uri());
    boolean isHealthCheck = webConfig.healthCheckPath.equals(req.uri());
    // send the default response for bad or health checks
    final HttpResponseStatus status;
    final byte[] content;
    final String contentType;
    if (isHealthCheck) {
      status = HttpResponseStatus.OK;
      content = ("HEALTHY:" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
      contentType = "text/text; charset=UTF-8";
    } else if (htmlResult != null) {
      status = HttpResponseStatus.OK;
      content = htmlResult.getBytes(StandardCharsets.UTF_8);
      contentType = "text/html; charset=UTF-8";
    } else {
      status = HttpResponseStatus.BAD_REQUEST;
      content = "<html><head><title>bad request</title></head><body>Greetings, this is primarily a websocket server, so your request made no sense. Sorry!</body></html>".getBytes(StandardCharsets.UTF_8);
      contentType = "text/html; charset=UTF-8";
    }
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), status, Unpooled.wrappedBuffer(content));
    HttpUtil.setContentLength(res, content.length);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    sendWithKeepAlive(webConfig, ctx, req, res);
  }

  private static void sendWithKeepAlive(
      final WebConfig webConfig,
      final ChannelHandlerContext ctx,
      final FullHttpRequest req,
      final FullHttpResponse res) {
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
}
