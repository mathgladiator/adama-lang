/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.ssl.SslContext;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.web.UriMatcher;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.ContentType;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.*;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.SpaceKeyRequest;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebMetrics;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DevBoxServiceBase implements ServiceBase {
  private final SimpleExecutor executor;
  private final DynamicControl control;
  private final TerminalIO io;
  private final WebConfig webConfig;
  private final AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle;
  private final File staticAssetRoot;
  private final File localLibAdamaJS;
  private final DevBoxAdamaMicroVerse verse;

  public DevBoxServiceBase(DynamicControl control, TerminalIO io, WebConfig webConfig, AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle, File staticAssetRoot, File localLibAdamaJS, DevBoxAdamaMicroVerse verse) {
    this.executor = SimpleExecutor.create("executor");
    this.control = control;
    this.io = io;
    this.webConfig = webConfig;
    this.bundle = bundle;
    this.staticAssetRoot = staticAssetRoot;
    this.localLibAdamaJS = localLibAdamaJS;
    this.verse = verse;
  }

  @Override
  public ServiceConnection establish(ConnectionContext context) {
    // if we have a service and a table, then let's use it!
    return new DevBoxAdama(executor, context, this.control, this.io, verse);
  }

  public void shutdown() {
    executor.shutdown();
  }

  @Override
  public HttpHandler http() {
    return new HttpHandler() {
      @Override
      public void handleDeepHealth(Callback<String> callback) {
        callback.success("DEVSERVER");
      }

      @Override
      public void handleOptions(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.failure(new ErrorCodeException(0));
      }

      @Override
      public void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.failure(new ErrorCodeException(0));
      }

      @Override
      public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        RxHTMLScanner.RxHTMLBundle current = bundle.get();
        if (current == null) {
          callback.failure(new ErrorCodeException(500));
          return;
        }
        if ("/template.js".equals(uri)) {
          callback.success(new HttpResult("text/javascript", current.forestJavaScript.getBytes(), false));
          return;
        }
        if ("/template.css".equals(uri)) {
          callback.success(new HttpResult("text/javascript", current.forestStyle.getBytes(), false));
          return;
        }
        if (uri != null && uri.endsWith("/devlibadama.js") && localLibAdamaJS != null) {
          StringBuilder js = new StringBuilder();
          try {
            js.append("/** tree.js **/\n\n");
            js.append(Files.readString(new File(localLibAdamaJS, "tree.js").toPath()));
            js.append("/** connection.js **/\n\n");
            String connection = Files.readString(new File(localLibAdamaJS, "connection.js").toPath());
            if (verse != null) {
              // TODO: decide on SSL for devbox
              connection = connection.replaceAll(Pattern.quote("\"wss://\""), Matcher.quoteReplacement("\"ws://\""));
            }
            js.append(connection);
            js.append("/** debugger.js **/\n\n");
            js.append(Files.readString(new File(localLibAdamaJS, "debugger.js").toPath()));
            js.append("/** rxhtml.js **/\n\n");
            String rxhtml = Files.readString(new File(localLibAdamaJS, "rxhtml.js").toPath());
            // TODO: have a way to point this to localhost:$port
            if (verse != null) {
              rxhtml = rxhtml.replaceAll(Pattern.quote("/*ENDPOINT=[*/Adama.Production/*]*/"), Matcher.quoteReplacement("\"localhost:8080\""));
              // TODO: decide on SSL for devbox
              rxhtml = rxhtml.replaceAll(Pattern.quote("\"https://\""), Matcher.quoteReplacement("\"http://\""));
            }
            js.append(rxhtml);
            callback.success(new HttpResult("text/javascript", js.toString().getBytes(), false));
          } catch (Exception ex) {
            callback.failure(new ErrorCodeException(500));
          }
          return;
        }

        // lame version for now, need to build a routable tree with type biases if this ever becomes a mainline
        for (UriMatcher matcher : current.matchers) {
          boolean result = matcher.matches(uri);
          if (result) {
            callback.success(new HttpResult("text/html", current.shell.getBytes(), false));
            return;
          }
        }
        if (uri.endsWith("/")) {
          uri += "index.html";
        }
        File file = new File(staticAssetRoot, uri.substring(1));
        try {
          if (file.exists()) {
            byte[] bytes = Files.readAllBytes(file.toPath());
            callback.success(new HttpResult(ContentType.of(uri), bytes, true));
          } else {
            callback.failure(new ErrorCodeException(404));
          }
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(500));
        }
      }


      private Callback<WebResponse> route(SpaceKeyRequest skr, Callback<HttpResult> callback) {
        return new Callback<>() {
          @Override
          public void success(WebResponse response) {
            if (response != null) {
              if ("text/agent".equals(response.contentType)) {
                String identity = "document/" + skr.space + "/" + skr.key + "/" + response.body;
                ObjectNode json = Json.newJsonObject();
                json.put("identity", identity);
                callback.success(new HttpResult("application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors));
              } else {
                if (response.asset != null) {
                  callback.success(new HttpResult(skr.space, skr.key, response.asset, response.cors));
                } else {
                  callback.success(new HttpResult(response.contentType, response.body.getBytes(StandardCharsets.UTF_8), response.cors));
                }
              }
            } else {
              callback.success(null);
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        };
      }

      @Override
      public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        if (verse != null) {
          SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
          Key key = new Key(skr.space, skr.key);
          WebPut webPut = new WebPut(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, headers, new NtDynamic(parametersJson), body);
          verse.service.webPut(key, webPut, route(skr, callback));
        } else {
          callback.failure(new ErrorCodeException(0));
        }
      }
    };
  }

  @Override
  public AssetSystem assets() {
    return null;
  }

  public Thread start() throws Exception {
    ServiceRunnable webServer = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), this, new CertificateFinder() {
      @Override
      public void fetch(String domain, Callback<SslContext> callback) {
        callback.success(null);
      }
    }, () -> {
    });
    Thread serviceThread = new Thread(webServer);
    serviceThread.start();
    webServer.waitForReady(1000);
    return serviceThread;
  }
}
