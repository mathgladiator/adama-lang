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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.adamalang.web.contracts.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final HttpHandler httpHandler;

  public WebHandler(WebConfig webConfig, WebMetrics metrics, HttpHandler httpHandler) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.httpHandler = httpHandler;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    HttpHandler.HttpResult httpResult = null;
    try {
      if (req.method() == HttpMethod.POST) {
        metrics.webhandler_post.run();
        HashMap<String, String> parameters = new HashMap<>();
        byte[] memory = new byte[req.content().readableBytes()];
        req.content().readBytes(memory);
        JsonNode node = new JsonMapper().readTree(memory);
        if (node.isObject()) {
          Iterator<Map.Entry<String, JsonNode>> it = node.fields();
          while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode value = entry.getValue();
            if (value != null && (value.isTextual() || value.isNumber())) {
              parameters.put(entry.getKey(), value.isTextual() ? value.textValue() : value.toString());
            }
          }
        }
        httpResult = httpHandler.handlePost(req.uri(), parameters);
      } else {
        metrics.webhandler_get.run();
        httpResult = httpHandler.handleGet(req.uri());
      }
    } catch (Exception exception) {
      httpResult = null;
      metrics.webhandler_exception.run();
    }
    if (httpResult != null) {
      metrics.webhandler_found.run();
    }
    boolean isHealthCheck = webConfig.healthCheckPath.equals(req.uri());
    // send the default response for bad or health checks
    final HttpResponseStatus status;
    final byte[] content;
    final String contentType;
    if (isHealthCheck) {
      metrics.webhandler_healthcheck.run();
      status = HttpResponseStatus.OK;
      content = ("HEALTHY:" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
      contentType = "text/text; charset=UTF-8";
    } else if (httpResult != null) {
      status = HttpResponseStatus.OK;
      content = httpResult.body;
      contentType = httpResult.contentType; //;
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

  private static void sendWithKeepAlive(final WebConfig webConfig, final ChannelHandlerContext ctx, final FullHttpRequest req, final FullHttpResponse res) {
    final var responseStatus = res.status();
    final var keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
    HttpUtil.setKeepAlive(res, keepAlive);
    final var future = ctx.writeAndFlush(res);
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }
}
