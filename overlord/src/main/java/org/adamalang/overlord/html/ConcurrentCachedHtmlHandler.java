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

import org.adamalang.web.contracts.HtmlHandler;

import java.util.concurrent.ConcurrentHashMap;

/** a very dumb handler with no logic; just show what is in the map */
public class ConcurrentCachedHtmlHandler implements HtmlHandler {
  private ConcurrentHashMap<String, String> uris;

  public ConcurrentCachedHtmlHandler() {
    this.uris = new ConcurrentHashMap<>();
  }

  @Override
  public String handle(String uri) {
    return uris.get(uri);
  }

  public void put(String uri, String html) {
    uris.put(uri, html);
  }
}
