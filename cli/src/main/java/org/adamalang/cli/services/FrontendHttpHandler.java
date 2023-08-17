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
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.mysql.impl.GlobalDomainFinder;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.model.Health;
import org.adamalang.mysql.model.Secrets;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.rxhtml.RxHtmlResult;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.service.KeyPrefixUri;
import org.adamalang.web.service.SpaceKeyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/** the http handler for the service */
public class FrontendHttpHandler implements HttpHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(FrontendHttpHandler.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  private final CommonServiceInit init;
  private final LocalRegionClient client;
  private final ConcurrentHashMap<String, Integer> spaceIds;
  private final DomainFinder domainFinder;

  public FrontendHttpHandler(CommonServiceInit init, LocalRegionClient client) {
    this.init = init;
    this.domainFinder = new GlobalDomainFinder(init.database);
    this.client = client;
    this.spaceIds = new ConcurrentHashMap<>();
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
              String identity = keyPair.signDocument(skr.space, skr.key, response.body);
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
    try {
      Integer spaceId = spaceIds.get(space);
      if (spaceId == null) {
        SpaceInfo spaceInfo = Spaces.getSpaceInfo(init.database, space);
        spaceId = spaceInfo.id;
        spaceIds.put(space, spaceInfo.id);
        // TODO: expire this after X minutes
      }
      String rxhtml = Spaces.getRxHtml(init.database, spaceId);
      if (rxhtml != null) {
        RxHtmlResult rxhtmlResult = RxHtmlTool.convertStringToTemplateForest(rxhtml, ShellConfig.start().end());
        // TODO: cache this along with a timestamp (OR, tie it to the document)
        if (rxhtmlResult.test(uri)) {
          String html = rxhtmlResult.shell.makeShell(rxhtmlResult);
          HttpResult result = new HttpResult("text/html", html.getBytes(StandardCharsets.UTF_8), false);
          callback.success(result);
          return;
        }
      }
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FRONTEND_FAILED_RXHTML_LOOKUP, ex, EXLOGGER));
      return;
    }
    get(new SpaceKeyRequest("ide", space, uri), headers, parametersJson, callback);
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
      health.append("<tr><td>Database Ping</td><td>" + Health.pingDataBase(init.database) + "</td>");
      init.engine.summarizeHtml((html) -> {
        health.append("<tr><td>Gossip</td><td>" + html + "</td>");
        // TODO: simplify for privacy of the service
        // TODO: check overlord is present
        // TODO: check if web and adama hosts is greated then a threshold; maybe, record a max and ensure we are at 80%+
        health.append("</table></body></html>\n");
        callback.success(health.toString());
      });
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(0, ex, EXLOGGER));
    }
  }
}
