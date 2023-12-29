/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.devbox.*;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.ContentType;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
  private final LocalAssets assets;
  private final boolean debuggerAvailable;
  private final ConcurrentHashMap<Integer, DevBoxAdama> inflight;
  private final AtomicInteger inflightId;
  private final RxPubSub rxPubSub;

  public DevBoxServiceBase(DynamicControl control, TerminalIO io, WebConfig webConfig, AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle, File staticAssetRoot, File localLibAdamaJS, File assetPath, DevBoxAdamaMicroVerse verse, boolean debuggerAvailable, RxPubSub rxPubSub) throws Exception {
    this.executor = SimpleExecutor.create("executor");
    this.control = control;
    this.io = io;
    this.webConfig = webConfig;
    this.bundle = bundle;
    this.staticAssetRoot = staticAssetRoot;
    this.localLibAdamaJS = localLibAdamaJS;
    this.verse = verse;
    this.assets = new LocalAssets(io, assetPath, verse.service);
    this.debuggerAvailable = debuggerAvailable;
    this.inflight = new ConcurrentHashMap<>();
    this.inflightId = new AtomicInteger(1);
    this.rxPubSub = rxPubSub;
  }

  public String diagnostics() {
    ObjectNode diag = Json.newJsonObject();
    diag.put("active-sockets", inflight.size());
    ObjectNode sockets = diag.putObject("sockets");
    for (Map.Entry<Integer, DevBoxAdama> entry : inflight.entrySet()) {
      entry.getValue().diagnostics(sockets.putObject("" + entry.getKey()));
    }
    return diag.toString();
  }

  @Override
  public ServiceConnection establish(ConnectionContext context) {
    // if we have a service and a table, then let's use it!
    int id = inflightId.incrementAndGet();
    DevBoxAdama devbox = new DevBoxAdama(executor, context, this.control, this.io, verse, () -> {
      inflight.remove(id);
    }, rxPubSub);
    inflight.put(id, devbox);
    return devbox;
  }

  public void shutdown() {
    executor.shutdown();
  }

  @Override
  public HttpHandler http() {
    return new HttpHandler() {

      @Override
      public void handle(ConnectionContext context, Method method, String identity, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        NtPrincipal who = NtPrincipal.NO_ONE;
        if (identity != null) {
          who = DevBoxAdama.principalOf(identity);
        }
        switch (method) {
          case PUT:
            handlePost(context, who, uri, headers, parametersJson, body, callback);
            return;
          case OPTIONS:
            handleOptions(context, who, uri, headers, parametersJson, callback);
            return;
          case DELETE:
            handleDelete(context, who, uri, headers, parametersJson, callback);
            return;
          case GET:
          default:
            handleGet(context, who, uri, headers, parametersJson, callback);
        }
      }

      @Override
      public void handleDeepHealth(Callback<String> callback) {
        callback.success("DEVSERVER");
      }

      public void handleOptions(ConnectionContext context, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.failure(new ErrorCodeException(0));
      }

      public void handleDelete(ConnectionContext context, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        callback.failure(new ErrorCodeException(0));
      }

      public void handleGet(ConnectionContext context, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
        RxHTMLScanner.RxHTMLBundle current = bundle.get();
        if (current == null) {
          callback.failure(new ErrorCodeException(500));
          return;
        }
        if (uri.startsWith("/~d/")) {
          if (verse != null && verse.domainKeyToUse != null) {
            SpaceKeyRequest skr = new SpaceKeyRequest(verse.domainKeyToUse.space, verse.domainKeyToUse.key, uri.substring(3));
            Key key = new Key(skr.space, skr.key);
            io.info("service|getting from document: " + skr.uri);

            WebGet webGet = new WebGet(new WebContext(who, context.origin, context.remoteIp), skr.uri, headers, new NtDynamic(parametersJson));
            verse.service.webGet(key, webGet, route(skr, callback));
            return;
          }
        }
        if ("/template.js".equals(uri)) {
          callback.success(new HttpResult("text/javascript", current.forestJavaScript.getBytes(), false));
          return;
        }
        if ("/template.css".equals(uri)) {
          callback.success(new HttpResult("text/javascript", current.forestStyle.getBytes(), false));
          return;
        }
        if (uri != null && uri.endsWith("/devlibadama-worker.js")) {
          StringBuilder js = new StringBuilder();
          try {
            if (localLibAdamaJS != null) {
              js.append(Files.readString(new File(localLibAdamaJS, "worker.js").toPath()));
            } else {
              js.append(JavaScriptResourcesRaw.WORKER);
            }
            callback.success(new HttpResult("text/javascript", js.toString().getBytes(), false));
          } catch (Exception ex) {
            callback.failure(new ErrorCodeException(500));
          }
          return;
        }
        if (uri != null && uri.endsWith("/devlibadama.js")) {
          StringBuilder js = new StringBuilder();
          try {
            js.append("/** tree.js **/\n\n");
            if (localLibAdamaJS != null) {
              js.append(Files.readString(new File(localLibAdamaJS, "tree.js").toPath()));
            } else {
              js.append(JavaScriptResourcesRaw.TREE);
            }
            js.append("/** connection.js **/\n\n");
            String connection;
            if (localLibAdamaJS != null) {
              connection = Files.readString(new File(localLibAdamaJS, "connection.js").toPath());
            } else {
              connection = JavaScriptResourcesRaw.CONNECTION;
            }
            if (verse != null) {
              // TODO: decide on SSL for devbox
              connection = connection.replaceAll(Pattern.quote("\"wss://\""), Matcher.quoteReplacement("\"ws://\""));
            }
            js.append(connection);
            if (debuggerAvailable) {
              js.append("/** debugger.js **/\n\n");
              if (localLibAdamaJS != null) {
                js.append(Files.readString(new File(localLibAdamaJS, "debugger.js").toPath()));
              } else {
                js.append(JavaScriptResourcesRaw.DEBUGGER);
              }
            }
            js.append("/** rxhtml.js **/\n\n");
            String rxhtml;
            if (localLibAdamaJS != null) {
              rxhtml = Files.readString(new File(localLibAdamaJS, "rxhtml.js").toPath());
            } else {
              rxhtml = JavaScriptResourcesRaw.RXHTML;
            }
            // TODO: have a way to point this to localhost:$port
            if (verse != null) {
              rxhtml = rxhtml.replaceAll(Pattern.quote("/*ENDPOINT=[*/Adama.Production/*]*/"), Matcher.quoteReplacement("location.host"));
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
        if (current.result.test(uri)) {
          callback.success(new HttpResult("text/html", current.shell.getBytes(), false));
          return;
        }
        if (uri.endsWith("/")) {
          uri += "index.html";
        }
        File file = new File(staticAssetRoot, uri.substring(1));
        if (file.exists() && file.isDirectory()) {
          uri += "/index.html";
          file = new File(staticAssetRoot, uri.substring(1));
        }
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
              } else if ("redirection/301".equals(response.contentType)) {
                callback.success(new HttpResult(response.body, 301));
              } else if ("redirection/302".equals(response.contentType)) {
                callback.success(new HttpResult(response.body, 302));
              } else {
                if (response.asset != null) {
                  callback.success(new HttpResult(skr.space, skr.key, response.asset, response.asset_transform, response.cors, 0));
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
            io.error("route failure: " + ex.code);
            callback.failure(ex);
          }
        };
      }

      public void handlePost(ConnectionContext context, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
        if (verse != null) {
          // TODO: differiate between a document/domain put
          final SpaceKeyRequest skr;
          if (verse.domainKeyToUse != null) {
            skr = new SpaceKeyRequest(verse.domainKeyToUse.space, verse.domainKeyToUse.key, uri);
          } else {
            skr = SpaceKeyRequest.parse(uri);
          }
          Key key = new Key(skr.space, skr.key);
          WebPut webPut = new WebPut(new WebContext(who, context.origin, context.remoteIp), skr.uri, headers, new NtDynamic(parametersJson), body);
          verse.service.webPut(key, webPut, route(skr, callback));
        } else {
          callback.failure(new ErrorCodeException(0));
        }
      }
    };
  }

  @Override
  public AssetSystem assets() {
    return assets;
  }

  public Thread start() throws Exception {
    ServiceRunnable webServer = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), this, (domain, callback) -> callback.success(null), new DomainFinder() {
      @Override
      public void find(String domain, Callback<Domain> callback) {
        if (verse != null && verse.domainKeyToUse != null) {
          callback.success(new Domain(domain, 0, verse.domainKeyToUse.space, verse.domainKeyToUse.key, true, null, null, 0));
        } else {
          callback.failure(new ErrorCodeException(-404));
        }
      }
     }, () -> {
    });
    Thread serviceThread = new Thread(webServer);
    serviceThread.start();
    webServer.waitForReady(1000);
    return serviceThread;
  }
}
