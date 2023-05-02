/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ProtectedUUID;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.*;
import org.adamalang.web.assets.cache.CachedAsset;
import org.adamalang.web.assets.cache.WebHandlerAssetCache;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.firewall.WebRequestShield;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class WebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static final Logger LOG = LoggerFactory.getLogger(WebHandler.class);

  private static final byte[] EMPTY_RESPONSE = new byte[0];
  private static final byte[] OK_RESPONSE = "OK".getBytes(StandardCharsets.UTF_8);
  private static final byte[] ASSET_FAILED_ATTACHMENT = ("<html><head><title>Asset Failure</title></head><body>Failure to initiate asset attachment.</body></html>").getBytes(StandardCharsets.UTF_8);
  private static final byte[] ASSET_COOKIE_LACKING = "<html><head><title>Bad Request</title></head><body>Asset cookie was not set.</body></html>".getBytes(StandardCharsets.UTF_8);
  private static final byte[] NOT_FOUND_RESPONSE = "<html><head><title>Bad Request; Not Found</title></head><body>Sorry, the request was not found within our handler space.</body></html>".getBytes(StandardCharsets.UTF_8);
  private static final byte[] ASSET_UPLOAD_FAILURE = "<html><head><title>Bad Request; Internal Error Uploading</title></head><body>Sorry, the upload failed.</body></html>".getBytes(StandardCharsets.UTF_8);
  private static final byte[] ASSET_UPLOAD_INCOMPLETE_FIELDS = "<html><head><title>Bad Request; Incomplete</title></head><body>Sorry, the post request was incomplete.</body></html>".getBytes(StandardCharsets.UTF_8);

  private final WebConfig webConfig;
  private final WebMetrics metrics;
  private final HttpHandler httpHandler;
  private final AssetSystem assets;
  private final WebHandlerAssetCache cache;

  public WebHandler(WebConfig webConfig, WebMetrics metrics, HttpHandler httpHandler, AssetSystem assets, WebHandlerAssetCache cache) {
    this.webConfig = webConfig;
    this.metrics = metrics;
    this.httpHandler = httpHandler;
    this.assets = assets;
    this.cache = cache;
  }

  /** internal: copy the origin to access control when allowed */
  private void transferCors(final HttpResponse res, final FullHttpRequest req, boolean allow) {
    String origin = req.headers().get(HttpHeaderNames.ORIGIN);
    if (origin != null && allow) { // CORS support directly
      res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
      res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
    }
  }

  /** send an immediate data result */
  private void sendImmediate(Runnable metric, FullHttpRequest req, final ChannelHandlerContext ctx, HttpResponseStatus status, byte[] content, String contentType, boolean cors) {
    metric.run();
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), status, Unpooled.wrappedBuffer(content));
    HttpUtil.setContentLength(res, content.length);
    if (contentType != null) {
      res.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }
    transferCors(res, req, cors);
    sendWithKeepAlive(webConfig, ctx, req, res);
  }

  private AssetStream streamOf(FullHttpRequest req, final ChannelHandlerContext ctx, boolean cors) {
    final boolean keepalive = HttpUtil.isKeepAlive(req);

    return new AssetStream() {
      private boolean started = false;
      private String contentType = null;
      private long contentLength;

      @Override
      public void headers(long contentLength, String contentType) {
        this.contentLength = contentLength;
        this.contentType = contentType;
      }

      private void setResponseHeaders(HttpResponse response) {
        if (this.contentLength < 0 && req.protocolVersion() == HttpVersion.HTTP_1_1) {
          response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        } else {
          if (this.contentLength >= 0) {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
          }
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        transferCors(response, req, cors);
      }

      @Override
      public void body(byte[] chunk, int offset, int length, boolean last) {
        if (!started && last) {
          byte[] content = Arrays.copyOfRange(chunk, offset, length);
          final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
          setResponseHeaders(res);
          sendWithKeepAlive(webConfig, ctx, req, res);
        } else {
          if (!started) {
            DefaultHttpResponse res = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.OK);
            setResponseHeaders(res);
            HttpUtil.setKeepAlive(res, keepalive);
            ctx.write(res);
            started = true;
          }
          if (chunk.length == length && offset == 0) {
            ctx.write(new DefaultHttpContent(Unpooled.wrappedBuffer(chunk)));
          } else {
            ctx.write(new DefaultHttpContent(Unpooled.wrappedBuffer(Arrays.copyOfRange(chunk, offset, length))));
          }
          if (last) {
            final var future = ctx.writeAndFlush(new DefaultLastHttpContent());
            if (!keepalive) {
              future.addListener(ChannelFutureListener.CLOSE);
            }
          }
        }
      }

      @Override
      public void failure(int code) {
        if (started) {
          ctx.close();
        } else {
          sendImmediate(metrics.webhandler_asset_failed, req, ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, ("Download asset failure:" + code).getBytes(StandardCharsets.UTF_8), "text/plain", false);
        }
      }
    };
  }

  /** handle a native asset */
  private void handleNtAsset(FullHttpRequest req, final ChannelHandlerContext ctx, Key key, NtAsset asset, boolean cors) {
    AssetStream response = streamOf(req, ctx, cors);

    if (!WebHandlerAssetCache.canCache(asset)) {
      // we can't cache? sad face -> stream direct
      assets.request(key, asset, response);
      return;
    }

    // if the response fails, for any reason, force the stream out of the cache to try again
    AssetStream wrapResponseToEvict = new AssetStream() {
      @Override
      public void headers(long length, String contentType) {
        response.headers(length, contentType);
      }

      @Override
      public void body(byte[] chunk, int offset, int length, boolean last) {
        response.body(chunk, offset, length, last);
      }

      @Override
      public void failure(int code) {
        response.failure(code);
        cache.failure(asset);
      }
    };

    // ask the cache for the cached asset
    cache.get(asset, new Callback<>() {
      @Override
      public void success(CachedAsset cachedAsset) {
        // attach the wrapped response to the asset
        AssetStream feed = cachedAsset.attachWhileInExecutor(wrapResponseToEvict);
        if (feed != null) {
          // pump the stream since this is the first requestor
          assets.request(key, asset, feed);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        response.failure(ex.code);
      }
    });
  }

  /** handle an asset request */
  @Deprecated
  private void handleAsset(FullHttpRequest req, final ChannelHandlerContext ctx, AssetRequest assetRequest, boolean cors) {
    assets.request(assetRequest, streamOf(req, ctx, cors));
  }

  /** handle secret and encrypted assets */
  private void handleEncryptedAsset(FullHttpRequest req, final ChannelHandlerContext ctx) {
    String assetKey = AssetRequest.extractAssetKey(req.headers().get(HttpHeaderNames.COOKIE));
    if (assetKey != null) {
      try {
        String encryptedId = req.uri().substring("/~assets/".length());
        metrics.webhandler_assets_start.run();
        handleAsset(req, ctx, AssetRequest.parse(encryptedId, assetKey), true);
      } catch (Exception err) {
        sendImmediate(metrics.webhandler_assets_failed_start, req, ctx, HttpResponseStatus.OK, ASSET_FAILED_ATTACHMENT, "text/html; charset=UTF-8", false);
      }
    } else {
      sendImmediate(metrics.webhandler_assets_no_cookie, req, ctx, HttpResponseStatus.OK, ASSET_COOKIE_LACKING, "text/html; charset=UTF-8", false);
    }
  }

  private void handleAssetUpload(final ChannelHandlerContext ctx, final FullHttpRequest req) {
    try {
      HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
      ArrayList<FileUpload> files = new ArrayList<>();
      String identity = null;
      String space = null;
      String key = null;
      for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
        switch (data.getHttpDataType()) {
          case Attribute:
            Attribute attribute = (Attribute) data;
            switch (attribute.getName()) {
              case "identity":
                identity = attribute.getValue();
                break;
              case "space":
                space = attribute.getValue();
                break;
              case "key":
                key = attribute.getValue();
                break;
            }
            break;
          case FileUpload:
            files.add((FileUpload) data);
            break;
          default:
            break;
        }
      }
      ConnectionContext context = ConnectionContextFactory.of(ctx, req.headers());
      if (identity != null && space != null && key != null) {
        Key uploadKey = new Key(space, key);
        // TODO Need a multi-latch to report success
        for (FileUpload upload : files) {
          AssetUploadBody body = new AssetUploadBody() {
            @Override
            public File getFileIfExists() {
              try {
                return upload.getFile();
              } catch (Exception ex) {
                return null;
              }
            }

            @Override
            public byte[] getBytes() {
              try {
                return upload.get();
              } catch (Exception ex) {
                return null;
              }
            }
          };
          AssetFact fact = AssetFact.of(body);
          NtAsset asset = new NtAsset(ProtectedUUID.generate(), upload.getFilename(), upload.getContentType(), fact.size, fact.md5, fact.sha384);
          final String identityFinal = identity;
          assets.upload(uploadKey, asset, body, new Callback<>() {
            @Override
            public void success(Void value) {
              assets.attach(identityFinal, context, uploadKey, asset, new Callback<Integer>() {
                @Override
                public void success(Integer value) {
                  // TODO: multi-latch report success
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  // TODO: multi-latch report failure @ attach
                }
              });
            }

            @Override
            public void failure(ErrorCodeException ex) {
              // TODO: multi-latch report failure @ upload
            }
          });
        }
        // TODO: leverage a multi-latch
        sendImmediate(metrics.webhandler_upload_asset_failure, req, ctx, HttpResponseStatus.OK, EMPTY_RESPONSE, "text/html; charset=UTF-8", true);
      } else {
        sendImmediate(metrics.webhandler_upload_asset_failure, req, ctx, HttpResponseStatus.BAD_REQUEST, ASSET_UPLOAD_INCOMPLETE_FIELDS, "text/html; charset=UTF-8", true);
      }
    } catch (Exception ex) {
      sendImmediate(metrics.webhandler_upload_asset_failure, req, ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, ASSET_UPLOAD_FAILURE, "text/html; charset=UTF-8", true);
    }
  }

  private boolean handleInternal(final ChannelHandlerContext ctx, final FullHttpRequest req) {
    if (webConfig.healthCheckPath.equals(req.uri())) { // health checks
      sendImmediate(metrics.webhandler_healthcheck, req, ctx, HttpResponseStatus.OK, ("HEALTHY:" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8), "text/text; charset=UTF-8", true);
      return true;
    } else if (webConfig.deepHealthCheckPath.equals(req.uri())) { // deep health check
      httpHandler.handleDeepHealth(new Callback<String>() {
        @Override
        public void success(String report) {
          sendImmediate(metrics.webhandler_deephealthcheck, req, ctx, HttpResponseStatus.OK, report.getBytes(StandardCharsets.UTF_8), "text/html", false);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          sendImmediate(metrics.webhandler_deephealthcheck, req, ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, ("Deep health failed!").getBytes(StandardCharsets.UTF_8), "text/html", false);
        }
      });
      return true;
    } else if (req.uri().startsWith("/~upload") && req.method() == HttpMethod.POST) {
      handleAssetUpload(ctx, req);
      return true;
    } else if (req.uri().startsWith("/libadama.js")) { // in-memory JavaScript library for the client
      sendImmediate(metrics.webhandler_client_download, req, ctx, HttpResponseStatus.OK, JavaScriptClient.ADAMA_JS_CLIENT_BYTES, "text/javascript; charset=UTF-8", true);
      return true;
    } else if (req.uri().startsWith("/~assets/")) { // assets that are encrypted and private to the connection
      handleEncryptedAsset(req, ctx);
      return true;
    } else if (req.uri().startsWith("/~p")) { // set an asset key
      final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(OK_RESPONSE));
      String value = req.uri().substring(3);
      String origin = req.headers().get(HttpHeaderNames.ORIGIN);
      if (origin != null) { // CORS support directly
        res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
      }
      res.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
      DefaultCookie cookie = new DefaultCookie("SAK", value);
      cookie.setSameSite(CookieHeaderNames.SameSite.None);
      cookie.setMaxAge(60 * 60 * 24 * 7);
      cookie.setHttpOnly(true);
      cookie.setSecure(true);
      res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
      res.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
      sendWithKeepAlive(webConfig, ctx, req, res);
      return true;
    }
    return false;
  }



  private void handleHttpResult(HttpHandler.HttpResult httpResultIncoming, final ChannelHandlerContext ctx, final FullHttpRequest req) {
    HttpHandler.HttpResult httpResult = httpResultIncoming;
    if (httpResult == null) { // no response found
      sendImmediate(metrics.webhandler_notfound, req, ctx, HttpResponseStatus.NOT_FOUND, NOT_FOUND_RESPONSE, "text/html; charset=UTF-8", true);
      return;
    }

    if (httpResult.asset != null && httpResult.space != null && httpResult.key != null) {
      handleNtAsset(req, ctx, new Key(httpResult.space, httpResult.key), httpResult.asset, httpResult.cors);
      return;
    }

    // otherwise, send the body
    metrics.webhandler_found.run();
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(httpResult.body));
    HttpUtil.setContentLength(res, httpResult.body.length);
    if (httpResult.contentType.length() > 0) {
      res.headers().set(HttpHeaderNames.CONTENT_TYPE, httpResult.contentType);
    }
    transferCors(res, req, httpResult.cors);
    sendWithKeepAlive(webConfig, ctx, req, res);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest req) throws Exception {
    // Step 1: Quick reject anything the shield doesn't like
    if (WebRequestShield.block(req.uri())) {
      sendImmediate(metrics.webhandler_firewall, req, ctx, HttpResponseStatus.GONE, EMPTY_RESPONSE, null, false);
      return;
    }

    // Step 2: Handle internal routing for Adama only stuff
    if (handleInternal(ctx, req)) {
      return;
    }

    // Step 4: Handle the result from the web request
    Callback<HttpHandler.HttpResult> callback = new Callback<>() {
      @Override
      public void success(HttpHandler.HttpResult value) {
        ctx.executor().execute(() -> {
          handleHttpResult(value, ctx, req);
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-handler:", ex);
        handleHttpResult(null, ctx, req);
      }
    };

    // Step 3: Parse the request and then route to the appropriate handler
    try {
      AdamaWebRequest wta = new AdamaWebRequest(req, ctx);
      if (req.method() == HttpMethod.OPTIONS) {
        metrics.webhandler_options.run();
        httpHandler.handleOptions(wta.uri, wta.headers, wta.parameters, callback);
      } else if (req.method() == HttpMethod.DELETE) {
        metrics.webhandler_delete.run();
        httpHandler.handleDelete(wta.uri, wta.headers, wta.parameters, callback);
      } else if (req.method() == HttpMethod.POST || req.method() == HttpMethod.PUT) {
        metrics.webhandler_post.run();
        httpHandler.handlePost(wta.uri, wta.headers, wta.parameters, wta.body, callback);
      } else {
        metrics.webhandler_get.run();
        httpHandler.handleGet(wta.uri, wta.headers, wta.parameters, callback);
      }
    } catch (Exception ex) {
      LOG.error("failure-to-build-wta:", ex);
      sendImmediate(metrics.webhandler_wta_crash, req, ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, EMPTY_RESPONSE, null, true);
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
