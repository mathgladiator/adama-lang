/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import io.netty.handler.ssl.SslContext;
import org.adamalang.api.ConnectionRouter;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.web.UriMatcher;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.ContentType;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebMetrics;

import java.io.File;
import java.nio.file.Files;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DevBoxServiceBase implements ServiceBase {

  private final WebConfig webConfig;
  private final AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle;
  private final File staticAssetRoot;
  private final File localLibAdamaJS;

  public DevBoxServiceBase(WebConfig webConfig, AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle, File staticAssetRoot, File localLibAdamaJS) {
    this.webConfig = webConfig;
    this.bundle = bundle;
    this.staticAssetRoot = staticAssetRoot;
    this.localLibAdamaJS = localLibAdamaJS;
  }

  @Override
  public ServiceConnection establish(ConnectionContext context) {
    DevBoxSocketAPI api = new DevBoxSocketAPI();
    ConnectionRouter router = new ConnectionRouter(null, null, api);
    return new ServiceConnection() {
      @Override
      public void execute(JsonRequest request, JsonResponder responder) {

      }

      @Override
      public boolean keepalive() {
        return true;
      }

      @Override
      public void kill() {

      }
    };
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
            js.append(Files.readString(new File(localLibAdamaJS, "connection.js").toPath()));
            js.append("/** debugger.js **/\n\n");
            js.append(Files.readString(new File(localLibAdamaJS, "debugger.js").toPath()));
            js.append("/** rxhtml.js **/\n\n");
            String rxhtml = Files.readString(new File(localLibAdamaJS, "rxhtml.js").toPath());
            // TODO: have a way to point this to localhost:$port
            rxhtml = rxhtml.replaceAll(Pattern.quote("/*ENDPOINT=[*/Adama.Production/*]*/"), Matcher.quoteReplacement("/*REPLACED*/Adama.Production"));
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

      @Override
      public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        callback.failure(new ErrorCodeException(0));
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
