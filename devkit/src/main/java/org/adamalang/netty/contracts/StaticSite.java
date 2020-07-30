/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.contracts;

import java.nio.charset.StandardCharsets;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

public interface StaticSite {
  public static FullHttpResponse ofHTML(final FullHttpRequest req, final String html) {
    final var content = html.getBytes(StandardCharsets.UTF_8);
    final FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
    HttpUtil.setContentLength(res, content.length);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
    return res;
  }

  public FullHttpResponse request(String path, HttpResponseStatus status, FullHttpRequest request) throws Exception;
}
