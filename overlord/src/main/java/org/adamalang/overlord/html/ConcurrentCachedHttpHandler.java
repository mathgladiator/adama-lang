/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.html;

import org.adamalang.web.contracts.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/** a very dumb handler with no logic; just show what is in the map */
public class ConcurrentCachedHttpHandler implements HttpHandler {
  private final ConcurrentHashMap<String, HttpResult> uris;

  public ConcurrentCachedHttpHandler() {
    this.uris = new ConcurrentHashMap<>();
  }

  @Override
  public HttpResult handle(String uri) {
    return uris.get(uri);
  }

  public void put(String uri, String html) {
    uris.put(uri, new HttpResult("text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8)));
  }

  public void put(String uri, HttpResult result) {
    uris.put(uri, result);
  }
}
