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
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.mysql.impl.GlobalDomainFinder;
import org.adamalang.mysql.impl.GlobalRxHtmlFetcher;
import org.adamalang.runtime.sys.domains.CachedDomainFinder;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.mysql.model.Health;
import org.adamalang.mysql.model.Secrets;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.runtime.sys.web.rxhtml.CachedRxHtmlFetcher;
import org.adamalang.runtime.sys.web.rxhtml.LiveSiteRxHtmlResult;
import org.adamalang.runtime.sys.web.rxhtml.RxHtmlFetcher;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.service.KeyPrefixUri;
import org.adamalang.web.service.SpaceKeyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/** the http handler for the service */
public class FrontendHttpHandler implements HttpHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(FrontendHttpHandler.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  private final CommonServiceInit init;
  private final LocalRegionClient client;
  private final DomainFinder domainFinder;
  private final RxHtmlFetcher rxHtmlFetcher;
  private final SimpleExecutor caches;
  private PrivateKeyWithId signingKey;

  public FrontendHttpHandler(CommonServiceInit init, LocalRegionClient client, PrivateKeyWithId signingKey) {
    this.caches = SimpleExecutor.create("cache-thread");
    this.init = init;
    this.domainFinder = new CachedDomainFinder(TimeSource.REAL_TIME, 1000, 5 * 60 * 1000, caches, new GlobalDomainFinder(init.database));
    this.client = client;
    this.rxHtmlFetcher = new CachedRxHtmlFetcher(TimeSource.REAL_TIME, 1000, 60 * 1000, caches, new GlobalRxHtmlFetcher(init.database));
    this.signingKey = signingKey;
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

  private Callback<WebResponse> route(SpaceKeyRequest skr, Callback<HttpResult> callback) {
    return new Callback<>() {
      @Override
      public void success(WebResponse response) {
        if (response != null) {
          if ("text/agent".equals(response.contentType)) {
            try {
              SigningKeyPair keyPair = Secrets.getOrCreateDocumentSigningKey(init.database, init.masterKey, skr.space, skr.key);

              String identity = signingKey.signDocumentIdentity(response.body, skr.space, skr.key, response.cache_ttl_seconds);
              ObjectNode json = Json.newJsonObject();
              json.put("identity", identity);
              callback.success(new HttpResult("application/json", json.toString().getBytes(StandardCharsets.UTF_8), response.cors));
            } catch (Exception ex) {
              callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FRONTEND_SECRETS_SIGNING_EXCEPTION, ex, EXLOGGER));
            }
          } else if ("text/identity".equals(response.contentType)) {
            ObjectNode json = Json.newJsonObject();
            json.put("identity", response.body);
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
  public void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      WebDelete delete = new WebDelete(contextOf(headers), skr.uri, headers, new NtDynamic(parametersJson));
      client.webDelete(skr.space, skr.key, delete, route(skr, callback));
    }
    callback.failure(new ErrorCodeException(-1));
  }

  private WebContext contextOf(TreeMap<String, String> headers) {
    return new WebContext(NtPrincipal.NO_ONE, headers.get("origin"), headers.get("remote-ip"));
  }

  private void get(SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    if (skr != null) {
      WebGet get = new WebGet(contextOf(headers), skr.uri, headers, new NtDynamic(parametersJson));
      client.webGet(skr.space, skr.key, get, route(skr, callback));
    } else {
      callback.success(null);
    }
  }

  private void getSpace(String space, String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    rxHtmlFetcher.fetch(space, new Callback<>() {
      @Override
      public void success(LiveSiteRxHtmlResult result) {
        if (result != null) {
          if (result.test(uri)) {
            callback.success(new HttpResult("text/html", result.html, false));
            return;
          }
        }
        get(new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    String host = headers.get("host");
    if (host != null) {
      boolean isSpecial = init.webConfig.specialDomains.contains(host);
      if (!isSpecial) {
        if (host.endsWith("." + init.webConfig.regionalDomain)) {
          get(SpaceKeyRequest.parse(uri), headers, parametersJson, callback);
          return;
        }

        for (String suffix : init.webConfig.globalDomains) {
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
  }

  private void post(SpaceKeyRequest skr, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    if (skr != null) {
      WebPut put = new WebPut(contextOf(headers), skr.uri, headers, new NtDynamic(parametersJson), body);
      client.webPut(skr.space, skr.key, put, route(skr, callback));
    } else {
      callback.success(null);
    }
  }

  @Override
  public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    String host = headers.get("host");
    if (host == null) {
      callback.failure(new ErrorCodeException(ErrorCodes.FRONTEND_POST_NO_HOST));
      return;
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

  @Override
  public void handleDeepHealth(Callback<String> callback) {
    try {
      final StringBuilder health = new StringBuilder();
      health.append("<!DOCTYPE html>\n<html><head><title>Adama Deep Health</title></head><body>\n");
      health.append("<h1>Adama Deep Health Check (for Humans!)</h1>\n");
      health.append("<table border=1>\n");
      // TODO: if global, then ping database
      // TODO: if region, then ping global region
      // TODO: abstract the health checks to be a set
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(0, ex, EXLOGGER));
    }
  }
}
