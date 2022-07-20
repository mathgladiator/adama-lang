/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMap;

import java.util.TreeMap;

/** represents a get request */
public class WebGet {
  public final NtClient who;
  public final String uri;
  public final WebRouter router;
  public final NtMap<String, String> headers;
  public final NtDynamic parameters;

  public WebGet(NtClient who, String uri, TreeMap<String, String> headers, NtDynamic parameters) {
    this.who = who;
    this.uri = uri;
    this.router = new WebRouter(uri);
    this.headers = new NtMap<>();
    this.headers.storage.putAll(headers);
    this.parameters = parameters;
  }
}
