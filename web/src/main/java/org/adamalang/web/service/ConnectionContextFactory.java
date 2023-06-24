/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.io.ConnectionContext;

import java.util.regex.Pattern;

/** read headers and ip into a ConnectionContext */
public class ConnectionContextFactory {
  public static ConnectionContext of(final ChannelHandlerContext ctx, HttpHeaders headers) {
    String origin = headers.get("origin");
    String ip = ctx.channel().remoteAddress().toString().replaceAll(Pattern.quote("/"), "");
    String xForwardedFor = headers.get("x-forwarded-for");
    if (xForwardedFor != null && !("".equals(xForwardedFor))) {
      ip = xForwardedFor;
    }
    String userAgent = headers.get(HttpHeaderNames.USER_AGENT);
    String assetKey = AssetRequest.extractAssetKey(headers.get(HttpHeaderNames.COOKIE));
    return new ConnectionContext(origin, ip, userAgent, assetKey);
  }
}
