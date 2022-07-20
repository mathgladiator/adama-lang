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

/** part of a URI. For example, if the URI = is /XYZ/123 then [XYZ] and [123] are fragments. */
public class WebFragment {
  public final String uri;
  public final String fragment;
  public final int tail;
  public final Boolean val_boolean;
  public final Integer val_int;
  public final Long val_long;
  public final Double val_double;

  public WebFragment(String uri, String fragment, int tail) {
    this.uri = uri;
    this.fragment = fragment;
    this.tail = tail;
    this.val_boolean = parseBoolean(fragment);
    this.val_int = parseInteger(fragment);
    this.val_long = parseLong(fragment);
    this.val_double = parseDouble(fragment);
  }

  private static final Boolean parseBoolean(String x) {
    if ("true".equalsIgnoreCase(x)) {
      return true;
    }
    if ("false".equalsIgnoreCase(x)) {
      return false;
    }
    return null;
  }

  private static final Integer parseInteger(String x) {
    try {
      return Integer.parseInt(x);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  private static final Long parseLong(String x) {
    try {
      return Long.parseLong(x);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  private static final Double parseDouble(String x) {
    try {
      return Double.parseDouble(x);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  public String tail() {
    return uri.substring(tail);
  }
}
