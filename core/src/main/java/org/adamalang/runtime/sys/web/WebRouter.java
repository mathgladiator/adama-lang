/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.web;

import java.util.ArrayList;

/** Tear down a URI into fragments */
public class WebRouter {
  public String uri;
  public WebFragment[] fragments;

  public WebRouter(String uri) {
    this.uri = uri;
    ArrayList<WebFragment> fragments = new ArrayList<>();
    int at = uri.indexOf('/') + 1; // skip the first slash
    while (at > 0) {
      int slashAt = uri.indexOf('/', at);
      if (slashAt >= 0) {
        fragments.add(new WebFragment(uri, uri.substring(at, slashAt), at));
        at = slashAt + 1;
      } else {
        fragments.add(new WebFragment(uri, uri.substring(at), at));
        at = -1;
      }
    }
    this.fragments = fragments.toArray(new WebFragment[fragments.size()]);
  }

  public WebFragment at(int k) {
    if (k < fragments.length) {
      return fragments[k];
    }
    return null;
  }
}
