/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
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

  public FrontendHttpHandler(AtomicBoolean alive, SimpleExecutor executor, WebConfig webConfig, DomainFinder domainFinder, RxHtmlFetcher rxHtmlFetcher, MultiRegionClient client, PrivateKeyWithId signingKey) {
    this.webConfig = webConfig;
    this.domainFinder = domainFinder;
    this.client = client;
    this.rxHtmlFetcher = rxHtmlFetcher;
    this.signingKey = signingKey;
    this.getCache = new HttpResultCache(TimeSource.REAL_TIME);
    HttpResultCache.sweeper(executor, alive, this.getCache, 500, 1500);
  }

  @Override
  public void handleOptions(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      WebGet get = new WebGet(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, new TreeMap<>(), new NtDynamic("{}"));
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
    if (IsIP.test(host)) {
      callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_IP_DONT_RESOLVE));
      return null;
    }
    if (host == null) {
      callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_NO_HOST_HEADER));
      return null;
    }
    return host;
  }

  @Override
  public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return; // handled by extractHost
    }

    boolean isSpecial = webConfig.specialDomains.contains(host);
    if (!isSpecial) {
      if (host.endsWith("." + webConfig.regionalDomain)) {
        get(SpaceKeyRequest.parse(uri), headers, parametersJson, callback);
        return;
      }

      for (String suffix : webConfig.globalDomains) {
        if (host.endsWith("." + suffix)) {
          String space = host.substring(0, host.length() - suffix.length() - 1);
          getSpace(space, uri, headers, parametersJson, callback);
          return;
        }
      }
    }
    domainFinder.find(host, new Callback<>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          if (domain.key != null && domain.routeKey) {
            SpaceKeyRequest skr = new SpaceKeyRequest(domain.space, domain.key, uri);
            get(skr, headers, parametersJson, callback);
          } else {
            getSpace(domain.space, uri, headers, parametersJson, callback);
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

  @Override
  public void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      WebDelete delete = new WebDelete(contextOf(headers), skr.uri, headers, new NtDynamic(parametersJson));
      client.webDelete(skr.space, skr.key, delete, route(skr, callback, null));
    }
    callback.failure(new ErrorCodeException(-1));
  }

  private WebContext contextOf(TreeMap<String, String> headers) {
    return new WebContext(NtPrincipal.NO_ONE, headers.get("origin"), headers.get("remote-ip"));
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
          } else if ("text/identity".equals(response.contentType)) {
            ObjectNode json = Json.newJsonObject();
            json.put("identity", response.body);
            result = new HttpResult("application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors);
          } else {
            if (response.asset != null) {
              result = new HttpResult(skr.space, skr.key, response.asset, response.cors);
            } else {
              result = new HttpResult(response.contentType, response.body.getBytes(StandardCharsets.UTF_8), response.cors);
            }
          }
          if (writeToCache != null && response.cache_ttl_seconds > 0) {
            writeToCache.accept(response.cache_ttl_seconds, result);
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

  @Override
  public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    String host = extractHost(headers, callback);
    if (host == null) {
      return;
    }
    if (host.endsWith("." + webConfig.regionalDomain)) {
      post(SpaceKeyRequest.parse(uri), headers, parametersJson, body, callback);
      return;
    }
    for (String suffix : webConfig.globalDomains) {
      if (host.endsWith("." + suffix)) {
        String space = host.substring(0, host.length() - suffix.length() - 1);
        post(new SpaceKeyRequest(space, "default-document", uri), headers, parametersJson, body, callback);
        return;
      }
    }
    domainFinder.find(host, new Callback<Domain>() {
      @Override
      public void success(Domain domain) {
        if (domain != null) {
          if (domain.key != null) {
            post(new SpaceKeyRequest(domain.space, domain.key, uri), headers, parametersJson, body, callback);
          } else {
            KeyPrefixUri kpu = KeyPrefixUri.fromCompleteUri(uri);
            post(new SpaceKeyRequest(domain.space, kpu.key, kpu.uri), headers, parametersJson, body, callback);
          }
        } else {
          post(SpaceKeyRequest.parse(uri), headers, parametersJson, body, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private void post(SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    if (skr != null) {
      WebPut put = new WebPut(contextOf(headers), skr.uri, headers, new NtDynamic(parametersJson), body);
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

  private void get(SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    if (skr != null) {
      WebGet get = new WebGet(contextOf(headers), skr.uri, headers, new NtDynamic(parametersJson));
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

  private void getSpace(String space, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    rxHtmlFetcher.fetch(space, new Callback<>() {
      @Override
      public void success(LiveSiteRxHtmlResult result) {
        if (result.test(uri)) {
          callback.success(new HttpResult("text/html", result.html, false));
          return;
        }
        get(new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        get(new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
      }
    });
  }
}
