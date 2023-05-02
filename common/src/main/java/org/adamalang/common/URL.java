/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import java.io.CharArrayWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** URL Encoding support */
public class URL {
  /** should the given character (c) not be encoded */
  public static boolean plain(int c, boolean ignoreSlashes) {
    return c == '/' && ignoreSlashes || 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' || c == '.' || c == '_' || c == '-' || c == '~';
  }

  /** urlencode the string */
  public static String encode(final String s, final boolean ignoreSlashes) {
    StringBuilder out = new StringBuilder(s.length());
    for (int j = 0; j < s.length();) {
      int c = s.charAt(j);
      if (plain(c, ignoreSlashes)) {
        out.append((char)c);
        j++;
      } else {
        CharArrayWriter buffer = new CharArrayWriter();
        do {
          buffer.write(c);
          if (c >= 0xD800 && c <= 0xDBFF) {
            if ( (j+1) < s.length()) {
              int d = s.charAt(j+1);
              if (d >= 0xDC00 && d <= 0xDFFF) {
                buffer.write(d);
                j++;
              }
            }
          }
          j++;
        } while (j < s.length() && !plain((c = s.charAt(j)), ignoreSlashes));
        buffer.flush();
        String str = buffer.toString();
        byte[] ba = str.getBytes(StandardCharsets.UTF_8);
        for (byte b : ba) {
          out.append("%");
          out.append(Hex.of(b).toUpperCase());
        }
      }
    }
    return out.toString();
  }

  /** encode a simple parameter map into a string */
  public static String parameters(Map<String, String> parameters) {
    if (parameters != null) {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (Map.Entry<String, String> param : parameters.entrySet()) {
        if (first) {
          sb.append("?");
          first = false;
        } else {
          sb.append("&");
        }
        sb.append(param.getKey()).append("=").append(URL.encode(param.getValue(), false));
      }
      return sb.toString();
    }
    return "";
  }
}
