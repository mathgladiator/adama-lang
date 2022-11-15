/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.html;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/** a very dumb handler with no logic; just show what is in the map */
public class ConcurrentCachedHttpHandler implements HttpHandler {
  private final ConcurrentHashMap<String, HttpResult> uris;

  public ConcurrentCachedHttpHandler() {
    this.uris = new ConcurrentHashMap<>();
  }

  @Override
  public void handleOptions(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    callback.success(new HttpResult("", new byte[0], false));
  }

  @Override
  public void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    callback.success(new HttpResult("", new byte[0], false));
  }

  @Override
  public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
    callback.success(uris.get(uri));
  }

  public void put(String uri, String html) {
    uris.put(uri, new HttpResult("text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8), false));
  }

  public void put(String uri, HttpResult result) {
    uris.put(uri, result);
  }

  @Override
  public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    callback.success(null);
  }

  @Override
  public void handleDeepHealth(Callback<String> callback) {
    callback.success("OVERLORD");
  }
}
