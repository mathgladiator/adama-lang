/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import java.util.Map;

/** a simplified http request */
public class SimpleHttpRequest {
  public final String method;
  public final String url;
  public final Map<String, String> headers;
  public final SimpleHttpRequestBody body;

  public SimpleHttpRequest(String method, String url, Map<String, String> headers, SimpleHttpRequestBody body) {
    this.method = method;
    this.url = url;
    this.headers = headers;
    this.body = body;
  }
}