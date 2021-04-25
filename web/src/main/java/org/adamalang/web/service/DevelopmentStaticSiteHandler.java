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
import io.netty.handler.codec.http.*;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class DevelopmentStaticSiteHandler implements UriHandler {
  private final File root;
  private final String absPathRoot;

  public DevelopmentStaticSiteHandler(File root) throws Exception {
    this.root = root;
    absPathRoot = root.getCanonicalPath();
  }

  @Override
  public void handle(FullHttpRequest input, QueryStringDecoder qsd, Callback<FullHttpResponse> callback) {
    try {
      if (input.method() != HttpMethod.GET) {
        callback.failure(new ErrorCodeException(400));
        return;
      }

      var toLoad = new File(root, qsd.path());
      if (toLoad.exists() && toLoad.isDirectory()) {
        toLoad = new File(toLoad, "index.html");
      }
      final var canonical = toLoad.getCanonicalPath();
      HttpResponseStatus status = HttpResponseStatus.OK;
      if (!canonical.startsWith(absPathRoot) || !toLoad.exists()) {
        toLoad = new File(root, "error.404.html");
        if (!toLoad.exists()) {
          callback.failure(new ErrorCodeException(404));
          return;
        }
        status = HttpResponseStatus.NOT_FOUND;
      }
      final var content = Files.readAllBytes(toLoad.toPath());
      final FullHttpResponse response = new DefaultFullHttpResponse(input.protocolVersion(), status, Unpooled.wrappedBuffer(content));
      final var contentType = InferContentType.fromFilename(toLoad.getName());
      if (contentType != null) {
        response.headers().set(CONTENT_TYPE, contentType);
      }
      HttpUtil.setContentLength(response, content.length);
      callback.success(response);
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(500, ex));
    }
  }
}
