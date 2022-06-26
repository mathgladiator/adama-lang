/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.service.AssetRequest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeMap;

public class MockServiceBase implements ServiceBase {
  @Override
  public ServiceConnection establish(ConnectionContext context) {
    return new ServiceConnection() {
      boolean alive = true;

      @Override
      public void execute(JsonRequest request, JsonResponder responder) {
        try {
          switch (request.method()) {
            case "cake":
            {
              responder.stream("{\"boss\":1}");
              responder.finish("{\"boss\":2}");
              return;
            }
            case "empty":
            {
              responder.finish("{}");
              return;
            }
            case "crash":
            {
              throw new NullPointerException();
            }
            case "kill":
              {
                responder.stream("{\"death\":1}");
                alive = false;
                return;
              }
            case "ex":
              {
                responder.error(new ErrorCodeException(1234));
                return;
              }
          }

        } catch (ErrorCodeException ex) {
          responder.error(ex);
        }
      }

      @Override
      public boolean keepalive() {
        return alive;
      }

      @Override
      public void kill() {}
    };
  }

  @Override
  public HttpHandler http() {
    return new HttpHandler() {
      @Override
      public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        if ("/foo".equals(uri)){
          callback.success(new HttpHandler.HttpResult("text/html; charset=UTF-8", "goo".getBytes(StandardCharsets.UTF_8)));
          return;
        }
        if ("/crash".equals(uri)) {
          callback.failure(new ErrorCodeException(-1));
          return;
        }
        callback.success(null);
      }

      @Override
      public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        if ("/body".equals(uri)){
          callback.success(new HttpHandler.HttpResult("text/html; charset=UTF-8", ("body:" + body).getBytes(StandardCharsets.UTF_8)));
          return;
        }
        if ("/crash".equals(uri)) {
          callback.failure(new ErrorCodeException(-1));
          return;
        }
        callback.success(null);
      }
    };
  }

  @Override
  public AssetDownloader downloader() {
    return new AssetDownloader() {
      @Override
      public void request(AssetRequest request, AssetStream stream) {
        if (request.key.equals("1")) {
          stream.headers(-1, "text/plain");
          byte[] chunk = "ChunkAndDone".getBytes(StandardCharsets.UTF_8);
          stream.body(chunk, 0, chunk.length, true);
          return;
        }
        if (request.key.equals("fail")) {
          stream.headers(-1,"text/plain");
          stream.failure(1234);
          return;
        }

        if (request.key.equals("incomplete")) {
          stream.headers(-1,"text/plain");
          byte[] chunk = "Chunk".getBytes(StandardCharsets.UTF_8);
          stream.body(chunk, 0, chunk.length, false);
          stream.failure(1234);
          return;
        }
        if (request.key.equals("3")) {
          stream.headers(-1, "text/plain");
          byte[] chunk1 = "Chunk1".getBytes(StandardCharsets.UTF_8);
          byte[] chunk2 = "Chunk2".getBytes(StandardCharsets.UTF_8);
          byte[] chunk3 = "Chunk3".getBytes(StandardCharsets.UTF_8);
          stream.body(chunk1, 0, chunk1.length, false);
          stream.body(chunk2, 0, chunk2.length, false);
          stream.body(chunk3, 0, chunk3.length, true);
          return;
        }
      }
    };
  }
}
