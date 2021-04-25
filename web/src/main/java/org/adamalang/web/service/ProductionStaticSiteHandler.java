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
import java.nio.file.Files;
import java.util.HashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class ProductionStaticSiteHandler implements UriHandler {
  private static class InMemoryFile {
    private final byte[] content;
    private final String contentType;

    private InMemoryFile(File file) throws Exception {
      this.contentType = InferContentType.fromFilename(file.getName());
      this.content = Files.readAllBytes(file.toPath());
    }
  }

  private static void fill(File root, String pathBase, HashMap<String, InMemoryFile> results) throws Exception {
    for(File f : root.listFiles()) {
      if (f.isDirectory()) {
        fill(f, pathBase + f.getName() + "/", results);
      } else {
        InMemoryFile data = new InMemoryFile(f);
        results.put(pathBase + f.getName(), data);
        if (f.getName().equals("index.html")) {
          results.put(pathBase, data);
          results.put(pathBase.substring(0, pathBase.length() - 1), data);
        }
      }
    }
  }

  public final HashMap<String, InMemoryFile> paths;

  public ProductionStaticSiteHandler(File root) throws Exception {
    this.paths = new HashMap<>();
    fill(root, "/", paths);
  }

  @Override
  public void handle(FullHttpRequest input, QueryStringDecoder qsd, Callback<FullHttpResponse> callback) {
    String path = qsd.path();
    while (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    if (input.method() != HttpMethod.GET) {
      callback.failure(new ErrorCodeException(400));
      return;
    }
    InMemoryFile file = paths.get(path);
    HttpResponseStatus status = HttpResponseStatus.OK;
    if (file == null) {
      file = paths.get("/error.404.html");
      status = HttpResponseStatus.NOT_FOUND;
    }
    if (file == null) {
      callback.failure(new ErrorCodeException(404));
      return;
    }
    final FullHttpResponse response = new DefaultFullHttpResponse(input.protocolVersion(), status, Unpooled.wrappedBuffer(file.content));
    if (file != null && file.contentType != null) {
      response.headers().set(CONTENT_TYPE, file.contentType);
    }
    HttpUtil.setContentLength(response, file.content.length);
    callback.success(response);
  }
}
