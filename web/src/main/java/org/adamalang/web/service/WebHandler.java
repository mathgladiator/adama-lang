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
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.contracts.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static final Logger LOG = LoggerFactory.getLogger(WebHandler.class);
  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final HttpHandler httpHandler;
  private final AssetDownloader downloader;

  public WebHandler(WebConfig webConfig, WebMetrics metrics, HttpHandler httpHandler, AssetDownloader downloader) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.httpHandler = httpHandler;
    this.downloader = downloader;
  }

  private void afterHttpResult(HttpHandler.HttpResult httpResultIncoming, final ChannelHandlerContext ctx, final FullHttpRequest req) {
    HttpHandler.HttpResult httpResult = httpResultIncoming;
    if (httpResult != null) {
      metrics.webhandler_found.run();
    }
    boolean isHealthCheck = webConfig.healthCheckPath.equals(req.uri());
    boolean isAdamaClient = "/libadama.js".equals(req.uri());
    boolean isSetAssetKey = req.uri().startsWith("/~p");
    boolean isAssetEncrypted = req.uri().startsWith("/~assets/");
    boolean isAssetRequestInHttpResult = httpResult != null && httpResult.asset != null;
    // send the default response for bad or health checks
    final HttpResponseStatus status;
    final byte[] content;
    final String contentType;
    if (isHealthCheck) {
      metrics.webhandler_healthcheck.run();
      status = HttpResponseStatus.OK;
      content = ("HEALTHY:" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
      contentType = "text/text; charset=UTF-8";
    } else if (isAdamaClient) {
      metrics.webhandler_client_download.run();
      status = HttpResponseStatus.OK;
      content = JavaScriptClient.ADAMA_JS_CLIENT_BYTES;
      contentType = "text/javascript; charset=UTF-8";
    } else if (isSetAssetKey) {
      metrics.webhandler_set_asset_key.run();
      status = HttpResponseStatus.OK;
      content = "OK".getBytes(StandardCharsets.UTF_8);
      contentType = "text/text; charset=UTF-8";
    } else if (isAssetEncrypted) {
      String assetKey = AssetRequest.extractAssetKey(req.headers().get(HttpHeaderNames.COOKIE));
      if (assetKey != null) {
        try {
          String encryptedId = req.uri().substring("/!assets/".length());
          metrics.webhandler_assets_start.run();
          AssetRequest assetRequest = AssetRequest.parse(encryptedId, assetKey);
          downloader.request(assetRequest, new AssetDownloader.AssetStream() {
            private boolean started = false;
            private String contentType = null;

            @Override
            public void headers(long length, String contentType) {
              this.contentType = contentType;
            }

            @Override
            public void body(byte[] chunk, int offset, int length, boolean last) {
              if (!started && last) {
                byte[] content = Arrays.copyOfRange(chunk, offset, length);
                final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
                HttpUtil.setContentLength(res, content.length);
                res.headers().set(HttpHeaderNames.CONTENT_TYPE, this.contentType);
                sendWithKeepAlive(webConfig, ctx, req, res);
              } else {
                if (!started) {
                  DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                  response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                  response.headers().set(HttpHeaderNames.CONTENT_TYPE, this.contentType);
                  ctx.write(response);
                  started = true;
                }
                ctx.write(new DefaultHttpContent(Unpooled.wrappedBuffer(Arrays.copyOfRange(chunk, offset, length))));
                if (last) {
                  ctx.writeAndFlush(new DefaultLastHttpContent());
                }
              }
            }

            @Override
            public void failure(int code) {
              if (started) {
                ctx.close();
              } else {
                byte[] content = ("Download asset failure:" + code).getBytes(StandardCharsets.UTF_8);
                final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.SERVICE_UNAVAILABLE, Unpooled.wrappedBuffer(content));
                res.headers().set("x-adama", "" + code);
                HttpUtil.setContentLength(res, content.length);
                res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                sendWithKeepAlive(webConfig, ctx, req, res);
              }
            }
          });
          return;
        } catch (Exception err) {
          metrics.webhandler_assets_failed_start.run();
          status = HttpResponseStatus.OK;
          content = ("<html><head><title>got asset request</title></head><body>Failure to initiate asset attachment.</body></html>").getBytes(StandardCharsets.UTF_8);
          contentType = "text/html; charset=UTF-8";
        }
      } else {
        metrics.webhandler_assets_no_cookie.run();
        status = HttpResponseStatus.BAD_REQUEST;
        content = "<html><head><title>bad request</title></head><body>Asset cookie was not set.</body></html>".getBytes(StandardCharsets.UTF_8);
        contentType = "text/html; charset=UTF-8";
      }
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
    if (isSetAssetKey) {
      String value = req.uri().substring(3);
      String origin = req.headers().get(HttpHeaderNames.ORIGIN);
      if (origin != null) {
        res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
      }
      DefaultCookie cookie = new DefaultCookie("SAK", value);
      cookie.setSameSite(CookieHeaderNames.SameSite.None);
      cookie.setMaxAge(60 * 60 * 24 * 7);
      cookie.setHttpOnly(true);
      cookie.setSecure(true);
      res.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
    }
    sendWithKeepAlive(webConfig, ctx, req, res);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    Callback<HttpHandler.HttpResult> callback = new Callback<HttpHandler.HttpResult>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        ctx.executor().execute(() -> {
          afterHttpResult(value, ctx, req);
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-handler: {}", ex);
        ctx.executor().execute(() -> {
          // this is a giant mess at the moment, fall back
          afterHttpResult(null, ctx, req);
        });
      }
    };

    final WebToAdama wta;
    try {
      wta = new WebToAdama(req);
    } catch (Exception ex) {
      LOG.error("failure-to-build-wta:{}", ex);
      byte[] content = new byte[0];
      final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(content));
      HttpUtil.setContentLength(res, content.length);
      res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
      sendWithKeepAlive(webConfig, ctx, req, res);
      return;
    }

    if (req.method() == HttpMethod.POST || req.method() == HttpMethod.PUT) {
      metrics.webhandler_post.run();
      httpHandler.handlePost(wta.uri, wta.headers, wta.parameters, wta.body, callback);
    } else {
      metrics.webhandler_get.run();
      httpHandler.handleGet(wta.uri, wta.headers, wta.parameters, callback);
    }
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
