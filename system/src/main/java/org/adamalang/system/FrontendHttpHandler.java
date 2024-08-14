/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.system;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.auth.AuthRequest;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.auth.Authenticator;
import org.adamalang.common.*;
import org.adamalang.common.keys.PrivateKeyWithId;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.runtime.sys.web.rxhtml.RxHtmlFetcher;
import org.adamalang.rxhtml.routing.Table;
import org.adamalang.rxhtml.routing.Target;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonLogger;
import org.adamalang.web.service.KeyPrefixUri;
import org.adamalang.web.service.SpaceKeyRequest;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.cache.HttpResultCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/** the http handler for the service */
public class FrontendHttpHandler implements HttpHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(FrontendHttpHandler.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  private final MultiRegionClient client;
  private final DomainFinder domainFinder;
  private final RxHtmlFetcher rxHtmlFetcher;
  private final WebConfig webConfig;
  private final PrivateKeyWithId signingKey;
  private final HttpResultCache getCache;
  private final Authenticator authenticator;
  private final JsonLogger accessLogger;

  public FrontendHttpHandler(AtomicBoolean alive, SimpleExecutor executor, WebConfig webConfig, DomainFinder domainFinder, RxHtmlFetcher rxHtmlFetcher, Authenticator authenticator, MultiRegionClient client, PrivateKeyWithId signingKey, JsonLogger accessLogger) {
    this.webConfig = webConfig;
    this.domainFinder = domainFinder;
    this.client = client;
    this.rxHtmlFetcher = rxHtmlFetcher;
    this.signingKey = signingKey;
    this.getCache = new HttpResultCache(TimeSource.REAL_TIME);
    this.authenticator = authenticator;
    this.accessLogger = accessLogger;
    HttpResultCache.sweeper(executor, alive, this.getCache, 500, 1500);
  }

  private void logBasics(ObjectNode logItem, Method method, String uri, TreeMap<String, String> headers, String parametersJson, String body) {
    logItem.put("@timestamp", LogTimestamp.now());
    logItem.put("method", method.name());
    logItem.put("uri", uri);
    logItem.put("handler", "http");
    if (headers != null) {
      String origin = headers.get("origin");
      if (origin != null) {
        logItem.put("origin", origin);
      }
    }
    if (parametersJson != null) {
      logItem.set("parameters", Json.parseJsonObject(parametersJson));
    }
    if (body != null) {
      logItem.put("body_size", body.length());
    }
  }

  private void handleWithPrincipal(Method method, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    ObjectNode logItem = Json.newJsonObject();
    logBasics(logItem, method, uri, headers, parametersJson, body);
    final long started = System.currentTimeMillis();
    logItem.put("agent", who.agent);
    logItem.put("authority", who.authority);
    Callback<HttpResult> wrapped = new Callback<HttpResult>() {
      @Override
      public void success(HttpResult result) {
        if (result != null) {
          logItem.put("success", true);
          result.logInto(logItem);
        } else {
          logItem.put("success", false);
        }
        callback.success(result);
        logItem.put("latency", System.currentTimeMillis() - started);
        accessLogger.log(logItem);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        logItem.put("success", false);
        logItem.put("failure-code", ex.code);
        callback.failure(ex);
      }
    };

    switch (method) {
      case PUT:
        handlePost(logItem, who, uri, headers, parametersJson, body, wrapped);
        return;
      case OPTIONS:
        handleOptions(logItem, who, uri, headers, parametersJson, wrapped);
        return;
      case DELETE:
        handleDelete(logItem, who, uri, headers, parametersJson, wrapped);
        return;
      case GET:
      default:
        handleGet(logItem, who, uri, headers, parametersJson, wrapped);
    }
  }

  @Override
  public void handle(ConnectionContext context, Method method, String identity, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    if (identity == null || "".equals(identity)) {
      NtPrincipal who = NtPrincipal.NO_ONE;
      handleWithPrincipal(method, who, uri, headers, parametersJson, body, callback);
    } else {
      authenticator.auth(new AuthRequest(identity, context), new Callback<AuthenticatedUser>() {
        @Override
        public void success(AuthenticatedUser user) {
         handleWithPrincipal(method, user.who, uri, headers, parametersJson, body, callback);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ObjectNode logItem = Json.newJsonObject();
          logItem.put("success", false);
          logItem.put("failure-code", ex.code);
          logBasics(logItem, method, uri, headers, parametersJson, body);
          callback.failure(ex);
        }
      });
    }
  }

  public void handleOptions(ObjectNode logItem, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return;
    }
    logItem.put("host", host);
    if (host.endsWith("." + webConfig.regionalDomain)) {
      options(logItem, who, SpaceKeyRequest.parse(uri), callback);
      return;
    }
    for (String suffix : webConfig.globalDomains) {
      if (host.endsWith("." + suffix)) {
        String space = host.substring(0, host.length() - suffix.length() - 1);
        String uriToRoute = uri;
        if (uriToRoute.startsWith("/~d/")) {
          uriToRoute = uriToRoute.substring(3);
        }
        options(logItem, who, new SpaceKeyRequest(space, "default-document", uriToRoute), callback);
        return;
      }
    }
    domainFinder.find(host, new Callback<>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          logItem.put("domain", domain.domain);
          String uriToRoute = uri;
          if (uriToRoute.startsWith("/~d/")) {
            uriToRoute = uriToRoute.substring(3);
          }
          if (domain.key != null) {
            options(logItem, who, new SpaceKeyRequest(domain.space, domain.key, uriToRoute), callback);
          } else {
            KeyPrefixUri kpu = KeyPrefixUri.fromCompleteUri(uri);
            options(logItem, who, new SpaceKeyRequest(domain.space, kpu.key, kpu.uri), callback);
          }
        } else {
          options(logItem, who, SpaceKeyRequest.parse(uri), callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private String extractHost(TreeMap<String, String> headers, Callback<HttpResult> callback) {
    String host = headers.get("host");
    if (host == null) {
      callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_NO_HOST_HEADER));
      return null;
    }
    if (host.indexOf(':') > 0) {
      host = host.split(Pattern.quote(":"))[0]; // throw away the port if put in
    }
    if (IsIP.test(host)) {
      callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_IP_DONT_RESOLVE));
      return null;
    }
    return host.trim().toLowerCase(Locale.ENGLISH);
  }

  public void handleGet(ObjectNode logItem, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return; // handled by extractHost
    }
    logItem.put("host", host);
    boolean isSpecial = webConfig.specialDomains.contains(host);
    if (!isSpecial) {
      if (host.endsWith("." + webConfig.regionalDomain)) {
        get(logItem, who, SpaceKeyRequest.parse(uri), headers, parametersJson, callback);
        return;
      }

      for (String suffix : webConfig.globalDomains) {
        if (host.endsWith("." + suffix)) {
          String space = host.substring(0, host.length() - suffix.length() - 1);
          if (uri.startsWith("/~d/")) {
            get(logItem, who, new SpaceKeyRequest(space, "default-document", uri.substring(3)), headers, parametersJson, callback);
          } else {
            getSpace(logItem, host, who, space, uri, headers, parametersJson, callback);
          }
          return;
        }
      }
    }
    domainFinder.find(host, new Callback<>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          logItem.put("owner", domain.owner);
          String uriToRoute = uri;
          boolean route = domain.routeKey;
          if (!domain.routeKey && uriToRoute.startsWith("/~d/")) {
            uriToRoute = uriToRoute.substring(3);
            route = true;
          }
          if (domain.key != null && route) {
            SpaceKeyRequest skr = new SpaceKeyRequest(domain.space, domain.key, uriToRoute);
            get(logItem, who, skr, headers, parametersJson, callback);
          } else {
            getSpace(logItem, host, who, domain.space, uri, headers, parametersJson, callback);
          }
        } else {
          LOGGER.error("domain-not-mapped:" + host);
          callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_NO_DOMAIN_MAPPING));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOGGER.error("failed-find-domain: " + host, ex);
        callback.failure(ex);
      }
    });
  }

  public void handleDelete(ObjectNode logItem, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      skr.logInto(logItem);
      WebDelete delete = new WebDelete(contextOf(who, headers), skr.uri, headers, new NtDynamic(parametersJson));
      client.webDelete(skr.space, skr.key, delete, route(skr, callback, null));
    } else {
      logItem.put("invalid", true);
      callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_DELETE_INVALID));
    }
  }

  private WebContext contextOf(NtPrincipal who, TreeMap<String, String> headers) {
    return new WebContext(who, headers.get("origin"), headers.get("remote-ip"));
  }

  private Callback<WebResponse> route(SpaceKeyRequest skr, Callback<HttpResult> callback, BiConsumer<Integer, HttpResult> writeToCache) {
    return new Callback<>() {
      @Override
      public void success(WebResponse response) {
        if (response != null) {
          final HttpResult result;
          if ("text/agent".equals(response.contentType)) {
            try {
              String identity = signingKey.signDocumentIdentity(response.body, skr.space, skr.key, response.cache_ttl_seconds);
              ObjectNode json = Json.newJsonObject();
              json.put("identity", identity);
              result = new HttpResult(200, "application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors);
            } catch (Exception ex) {
              callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FRONTEND_SECRETS_SIGNING_EXCEPTION, ex, EXLOGGER));
              return;
            }
          } else {
            result = commonRoute(response, skr);
          }
          if (writeToCache != null && response.cache_ttl_seconds > 0) {
            writeToCache.accept(response.cache_ttl_seconds * 1000, result);
          }
          callback.success(result);
        } else {
          callback.success(new HttpResult(404, "text/plain", "not found".getBytes(StandardCharsets.UTF_8), true));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        int status = KnownErrors.inferHttpStatusCodeFrom(ex.code);
        callback.success(new HttpResult(status, "text/plain", ("error:" + ex.code).getBytes(StandardCharsets.UTF_8), true));
      }
    };
  }

  public void handlePost(ObjectNode logItem, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return;
    }
    logItem.put("host", host);
    if (host.endsWith("." + webConfig.regionalDomain)) {
      post(logItem, who, SpaceKeyRequest.parse(uri), headers, parametersJson, body, callback);
      return;
    }
    for (String suffix : webConfig.globalDomains) {
      if (host.endsWith("." + suffix)) {
        String space = host.substring(0, host.length() - suffix.length() - 1);
        post(logItem, who, new SpaceKeyRequest(space, "default-document", uri), headers, parametersJson, body, callback);
        return;
      }
    }
    domainFinder.find(host, new Callback<Domain>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          logItem.put("domain", domain.domain);
          if (domain.key != null) {
            post(logItem, who, new SpaceKeyRequest(domain.space, domain.key, uri), headers, parametersJson, body, callback);
          } else {
            KeyPrefixUri kpu = KeyPrefixUri.fromCompleteUri(uri);
            post(logItem, who, new SpaceKeyRequest(domain.space, kpu.key, kpu.uri), headers, parametersJson, body, callback);
          }
        } else {
          post(logItem, who, SpaceKeyRequest.parse(uri), headers, parametersJson, body, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private void post(ObjectNode logItem, NtPrincipal who, SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    if (skr != null) {
      skr.logInto(logItem);
      WebPut put = new WebPut(contextOf(who, headers), skr.uri, headers, new NtDynamic(parametersJson), body);
      client.webPut(skr.space, skr.key, put, route(skr, callback, null));
    } else {
      callback.success(null);
    }
  }

  private void options(ObjectNode logItem, NtPrincipal who, SpaceKeyRequest skr, Callback<HttpResult> callback) {
    if (skr != null) {
      skr.logInto(logItem);
      WebGet get = new WebGet(new WebContext(who, "origin", "ip"), skr.uri, new TreeMap<>(), new NtDynamic("{}"));
      client.webOptions(skr.space, skr.key, get, new Callback<>() {
        @Override
        public void success(WebResponse value) {
          callback.success(new HttpResult(200, "", null, value.cors));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    } else {
      logItem.put("invalid", true);
      callback.success(new HttpResult(404, "", null, false));
    }
  }

  @Override
  public void handleDeepHealth(Callback<String> callback) {
    try {
      String health = "<!DOCTYPE html>\n<html><head><title>Adama Deep Health</title></head><body>\n" + "<h1>Adama Deep Health Check (for Humans!)</h1>\n" + "<table border=1>\n";
      // TODO: if global, then ping database
      // TODO: if region, then ping global region
      // TODO: abstract the health checks to be a set
      callback.success(health);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(0, ex, EXLOGGER));
    }
  }

  private void get(ObjectNode logItem, NtPrincipal who, SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    if (skr != null) {
      skr.logInto(logItem);
      WebGet get = new WebGet(contextOf(who, headers), skr.uri, headers, new NtDynamic(parametersJson));
      String cacheKey = skr.cacheKey(parametersJson);
      HttpResult cachedResult = getCache.get(cacheKey);
      if (cachedResult != null) {
        // TODO: create a metering record for bandwidth
        callback.success(cachedResult);
        return;
      }
      client.webGet(skr.space, skr.key, get, route(skr, callback, getCache.inject(cacheKey)));
    } else {
      logItem.put("invalid", true);
      callback.success(null);
    }
  }

  public static TreeMap<String, String> prepareCapture(String host) {
    TreeMap<String, String> captured = new TreeMap<>();
    if (host != null) {
      captured.put("$host", host);
      String[] split = host.split(Pattern.quote("."));
      if (split.length > 1) {
        captured.put("$host.apex", split[split.length - 2] + "." + split[split.length - 1]);
      }
      if (split.length > 2) {
        captured.put("$host.sub", split[split.length - 3]);
      }
    }
    return captured;
  }

  public static HttpResult convertRxHTMLTargetToHttpResult(Target targetRaw, TreeMap<String, String> captured) {
    Target target = targetRaw;
    if (target.mutation != null) { // mutate the target based on the captured variables
      target = target.mutation.apply(target, captured);
    }
    String contentType = "application/octet-stream";
    if (target.headers != null) {
      String newContentType = target.headers.remove("context/type");
      if (newContentType != null) {
        contentType = newContentType;
      }
      String location = target.headers.remove("location");
      if ((target.status == 301 || target.status == 302) && location != null) {
        return new HttpResult(location, target.status);
      }
    }
    return new HttpResult(target.status, contentType, target.body, false, target.headers);
  }

  private void getSpace(ObjectNode logInfo, String host, NtPrincipal who, String space, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    rxHtmlFetcher.fetch(space, new Callback<>() {
      @Override
      public void success(Table result) {
        TreeMap<String, String> captured = prepareCapture(host);
        Target found = result.route(uri, captured);
        if (found != null) {
          logInfo.put("rxhtml", true);
          callback.success(convertRxHTMLTargetToHttpResult(found, captured));
        } else {
          logInfo.put("rxhtml", false);
          get(logInfo, who, new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        get(logInfo, who, new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
      }
    });
  }

  public static HttpResult commonRoute(WebResponse response, SpaceKeyRequest skr) {
    if ("redirection/301".equals(response.contentType)) {
      return new HttpResult(response.body, 301);
    } else if ("redirection/302".equals(response.contentType)) {
      return new HttpResult(response.body, 302);
    } else if ("text/identity".equals(response.contentType)) {
      ObjectNode json = Json.newJsonObject();
      json.put("identity", response.body);
      return new HttpResult(200, "application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors);
    } else {
      if (response.asset != null) {
        return new HttpResult(response.status, skr.space, skr.key, response.asset, response.asset_transform, response.cors, response.cache_ttl_seconds);
      } else {
        if (response.body != null) {
          return new HttpResult(response.status, response.contentType, response.body.getBytes(StandardCharsets.UTF_8), response.cors);
        } else {
          return new HttpResult(response.status, response.contentType, null, response.cors);
        }
      }
    }
  }
}
