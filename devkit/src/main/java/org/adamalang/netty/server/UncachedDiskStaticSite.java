/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import java.io.File;
import java.nio.file.Files;
import org.adamalang.netty.contracts.StaticSite;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

public class UncachedDiskStaticSite implements StaticSite {
  public final String absPathRoot;
  public final File root;

  public UncachedDiskStaticSite(final File root) throws Exception {
    this.root = root;
    absPathRoot = root.getCanonicalPath();
  }

  @Override
  public FullHttpResponse request(final String path, final HttpResponseStatus status, final FullHttpRequest request) throws Exception {
    var toLoad = new File(root, path);
    if (toLoad.exists() && toLoad.isDirectory()) {
      toLoad = new File(toLoad, "index.html");
    }
    final var canonical = toLoad.getCanonicalPath();
    if (!canonical.startsWith(absPathRoot)) {
      // SECURITY PROBLEM: 404 for opaqueness
      return null;
    }
    if (!toLoad.exists()) {
      // 404
      return null;
    }
    final var content = Files.readAllBytes(toLoad.toPath());
    final FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), status, Unpooled.copiedBuffer(content));
    final var contentType = InferContentType.fromFilename(toLoad.getName());
    if (contentType != null) {
      response.headers().set(CONTENT_TYPE, contentType);
    }
    HttpUtil.setContentLength(response, content.length);
    return response;
  }
}
