/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands.services;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.data.Domain;
import org.adamalang.mysql.model.Domains;
import org.adamalang.net.client.Client;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.service.SpaceKeyRequest;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/** the http handler for the service */
public class FrontendHttpHandler implements HttpHandler {
  private final CommonServiceInit init;
  private final Client client;

  public FrontendHttpHandler(CommonServiceInit init, Client client) {
    this.init = init;
    this.client = client;
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
    return new Callback<WebResponse>() {
      @Override
      public void success(WebResponse response) {
        if (response != null) {
          if (response.asset != null) {
            callback.success(new HttpResult(skr.space, skr.key, response.asset, response.cors));
          } else {
            callback.success(new HttpResult(response.contentType, response.body.getBytes(StandardCharsets.UTF_8), response.cors));
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
      WebDelete delete = new WebDelete(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, headers, new NtDynamic(parametersJson));
      client.webDelete(skr.space, skr.key, delete, route(skr, callback));
    }
    callback.failure(new ErrorCodeException(-1));
  }

  @Override
  public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    /*
    try {
      String host = headers.get("host");
      Domain domain = Domains.get(init.database, host);
      // TODO: cache the domain for faster lookup (if not null) (if not, cache a negative result for 5 minutes)
      // TODO: get the space's RxHTML, parse the tree, and return shell if the shell is applicable
      // TODO: if shell is not applicable, then route to ("ide", $space, $uri) a SpaceKeyRequest
    } catch (Exception ex) {

    }
    */

    // TODO: look up domain, If domain was found, then
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      // TODO: need a way to get an NtPrincipal token
      WebGet get = new WebGet(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, headers, new NtDynamic(parametersJson));
      client.webGet(skr.space, skr.key, get, route(skr, callback));
    } else {
      callback.success(null);
    }
  }

  @Override
  public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    SpaceKeyRequest skr = SpaceKeyRequest.parse(uri);
    if (skr != null) {
      // TODO: need a way to get an NtPrincipal token
      WebPut put = new WebPut(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), skr.uri, headers, new NtDynamic(parametersJson), body);
      client.webPut(skr.space, skr.key, put, route(skr, callback));
    } else {
      callback.success(null);
    }
  }
}
