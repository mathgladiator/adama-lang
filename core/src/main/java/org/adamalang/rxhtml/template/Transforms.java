/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import java.util.Locale;

public class Transforms {
  public static String of(String transform) {
    switch (transform.trim().toLowerCase(Locale.ROOT)) {
      case "principal.agent":
        return "function(x) { return x.agent; }";
      case "principal.authority":
        return "function(x) { return x.authority; }";
      case "trim":
        return "function(x) { return ('' + x).trim(); }";
      case "upper":
        return "function(x) { return ('' + x).toUpperCase(); }";
      case "lower":
        return "function(x) { return ('' + x).toLowerCase(); }";
      default:
        return "function(x) { return x; }";
    }
  }
}
