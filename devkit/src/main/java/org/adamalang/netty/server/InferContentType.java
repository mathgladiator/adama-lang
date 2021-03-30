/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

public class InferContentType {
  public static String fromFilename(final String name) {
    final var dotK = name.lastIndexOf('.');
    if (dotK < 0) { return null; }
    final var ext = name.substring(dotK + 1);
    switch (ext) {
      case "html":
      case "htm":
        return "text/html; charset=UTF-8";
      case "js":
        return "text/javascript";
      case "webp":
        return "image/webp";
      case "css":
        return "text/css";
      case "png":
        return "image/png";
      case "jpeg":
      case "jpg":
        return "image/jpeg";
      case "wasm":
        return "application/wasm";
    }
    return null;
  }
}
