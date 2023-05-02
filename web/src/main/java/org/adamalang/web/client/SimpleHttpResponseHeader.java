/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import java.util.Map;

/** simplified http response */
public class SimpleHttpResponseHeader {
  public final int status;
  public final Map<String, String> headers;

  public SimpleHttpResponseHeader(int status, Map<String, String> headers) {
    this.status = status;
    this.headers = headers;
  }
}
