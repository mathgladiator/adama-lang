package org.adamalang.rxhtml.template;

import java.util.Locale;

public class Transforms {
  public static String of(String transform) {
    switch (transform.trim().toLowerCase(Locale.ROOT)) {
      case "ntclient.agent":
        return "function(x) { return x.agent; }";
      case "ntclient.authority":
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
