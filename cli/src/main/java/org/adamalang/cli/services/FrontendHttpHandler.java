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
package org.adamalang.cli.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.auth.AuthRequest;
import org.adamalang.auth.Authenticator;
import org.adamalang.common.*;
import org.adamalang.common.keys.PrivateKeyWithId;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.runtime.sys.web.rxhtml.LiveSiteRxHtmlResult;
import org.adamalang.runtime.sys.web.rxhtml.RxHtmlFetcher;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.service.KeyPrefixUri;
import org.adamalang.web.service.SpaceKeyRequest;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.cache.HttpResultCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
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

  public FrontendHttpHandler(AtomicBoolean alive, SimpleExecutor executor, WebConfig webConfig, DomainFinder domainFinder, RxHtmlFetcher rxHtmlFetcher, Authenticator authenticator, MultiRegionClient client, PrivateKeyWithId signingKey) {
    this.webConfig = webConfig;
    this.domainFinder = domainFinder;
    this.client = client;
    this.rxHtmlFetcher = rxHtmlFetcher;
    this.signingKey = signingKey;
    this.getCache = new HttpResultCache(TimeSource.REAL_TIME);
    this.authenticator = authenticator;
    HttpResultCache.sweeper(executor, alive, this.getCache, 500, 1500);
  }

  private void handleWithPrincipal(Method method, NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    switch (method) {
      case PUT:
        handlePost(who, uri, headers, parametersJson, body, callback);
        return;
      case OPTIONS:
        handleOptions(who, uri, headers, parametersJson, callback);
        return;
      case DELETE:
        handleDelete(who, uri, headers, parametersJson, callback);
        return;
      case GET:
      default:
        handleGet(who, uri, headers, parametersJson, callback);
    }
  }
  @Override
  public void handle(Method method, String identity, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    NtPrincipal who = NtPrincipal.NO_ONE;
    handleWithPrincipal(method, who, uri, headers, parametersJson, body, callback);
    /*
    if (identity == null || "".equals(identity)) {
      // USE NO_ONE
    } else {
      authenticator.auth(new AuthRequest(identity, new ConnectionContext()... shit, need the context in the plumbing
    }
    */
  }

  public void handleOptions(NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      WebGet get = new WebGet(new WebContext(who, "origin", "ip"), skr.uri, new TreeMap<>(), new NtDynamic("{}"));
      client.webOptions(skr.space, skr.key, get, new Callback<>() {
        @Override
        public void success(WebResponse value) {
          callback.success(new HttpResult("", null, value.cors));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    } else {
      callback.success(new HttpResult("", null, false));
    }
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
    return host;
  }

  public void handleGet(NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return; // handled by extractHost
    }

    boolean isSpecial = webConfig.specialDomains.contains(host);
    if (!isSpecial) {
      if (host.endsWith("." + webConfig.regionalDomain)) {
        get(who, SpaceKeyRequest.parse(uri), headers, parametersJson, callback);
        return;
      }

      for (String suffix : webConfig.globalDomains) {
        if (host.endsWith("." + suffix)) {
          String space = host.substring(0, host.length() - suffix.length() - 1);
          if (uri.startsWith("/~d/")) {
            get(who, new SpaceKeyRequest(space, "default-document", uri.substring(3)), headers, parametersJson, callback);
          } else {
            getSpace(who, space, uri, headers, parametersJson, callback);
          }
          return;
        }
      }
    }
    domainFinder.find(host, new Callback<>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          String uriToRoute = uri;
          boolean route = domain.routeKey;
          if (!domain.routeKey && uriToRoute.startsWith("/~d/")) {
            uriToRoute = uriToRoute.substring(3);
            route = true;
          }
          if (domain.key != null && route) {
            SpaceKeyRequest skr = new SpaceKeyRequest(domain.space, domain.key, uriToRoute);
            get(who, skr, headers, parametersJson, callback);
          } else {
            getSpace(who, domain.space, uri, headers, parametersJson, callback);
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

  public void handleDelete(NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      WebDelete delete = new WebDelete(contextOf(who, headers), skr.uri, headers, new NtDynamic(parametersJson));
      client.webDelete(skr.space, skr.key, delete, route(skr, callback, null));
    }
    callback.failure(new ErrorCodeException(-1));
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
              result = new HttpResult("application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors);
            } catch (Exception ex) {
              callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FRONTEND_SECRETS_SIGNING_EXCEPTION, ex, EXLOGGER));
              return;
            }
          } else if ("redirection/301".equals(response.contentType)) {
            result = new HttpResult(response.body, 301);
          } else if ("redirection/302".equals(response.contentType)) {
            result = new HttpResult(response.body, 302);
          } else if ("text/identity".equals(response.contentType)) {
            ObjectNode json = Json.newJsonObject();
            json.put("identity", response.body);
            result = new HttpResult("application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors);
          } else {
            if (response.asset != null) {
              result = new HttpResult(skr.space, skr.key, response.asset, response.asset_transform, response.cors, response.cache_ttl_seconds);
            } else {
              result = new HttpResult(response.contentType, response.body.getBytes(StandardCharsets.UTF_8), response.cors);
            }
          }
          if (writeToCache != null && response.cache_ttl_seconds > 0) {
            writeToCache.accept(response.cache_ttl_seconds * 1000, result);
          }
          callback.success(result);
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

  public void handlePost(NtPrincipal who, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return;
    }
    if (host.endsWith("." + webConfig.regionalDomain)) {
      post(who, SpaceKeyRequest.parse(uri), headers, parametersJson, body, callback);
      return;
    }
    for (String suffix : webConfig.globalDomains) {
      if (host.endsWith("." + suffix)) {
        String space = host.substring(0, host.length() - suffix.length() - 1);
        post(who, new SpaceKeyRequest(space, "default-document", uri), headers, parametersJson, body, callback);
        return;
      }
    }
    domainFinder.find(host, new Callback<Domain>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          if (domain.key != null) {
            post(who, new SpaceKeyRequest(domain.space, domain.key, uri), headers, parametersJson, body, callback);
          } else {
            KeyPrefixUri kpu = KeyPrefixUri.fromCompleteUri(uri);
            post(who, new SpaceKeyRequest(domain.space, kpu.key, kpu.uri), headers, parametersJson, body, callback);
          }
        } else {
          post(who, SpaceKeyRequest.parse(uri), headers, parametersJson, body, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private void post(NtPrincipal who, SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    if (skr != null) {
      WebPut put = new WebPut(contextOf(who, headers), skr.uri, headers, new NtDynamic(parametersJson), body);
      client.webPut(skr.space, skr.key, put, route(skr, callback, null));
    } else {
      callback.success(null);
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

  private void get(NtPrincipal who, SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    if (skr != null) {
      WebGet get = new WebGet(contextOf(who, headers), skr.uri, headers, new NtDynamic(parametersJson));
      String cacheKey = skr.cacheKey(parametersJson);
      HttpResult cachedResult = getCache.get(cacheKey);
      if (cachedResult != null) {
        callback.success(cachedResult);
        return;
      }
      client.webGet(skr.space, skr.key, get, route(skr, callback, getCache.inject(cacheKey)));
    } else {
      callback.success(null);
    }
  }

  private void getSpace(NtPrincipal who, String space, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    rxHtmlFetcher.fetch(space, new Callback<>() {
      @Override
      public void success(LiveSiteRxHtmlResult result) {
        if (result.test(uri)) {
          callback.success(new HttpResult("text/html", result.html, false));
          return;
        }
        get(who, new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        get(who, new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
      }
    });
  }
}
