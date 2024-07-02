/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.common;

import java.io.CharArrayWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** URL Encoding support */
public class URL {
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

  /** urlencode the string */
  public static String encode(final String s, final boolean ignoreSlashes) {
    StringBuilder out = new StringBuilder(s.length());
    for (int j = 0; j < s.length(); ) {
      int c = s.charAt(j);
      if (plain(c, ignoreSlashes)) {
        out.append((char) c);
        j++;
      } else {
        CharArrayWriter buffer = new CharArrayWriter();
        do {
          buffer.write(c);
          if (c >= 0xD800 && c <= 0xDBFF) {
            if ((j + 1) < s.length()) {
              int d = s.charAt(j + 1);
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

  /** should the given character (c) not be encoded */
  public static boolean plain(int c, boolean ignoreSlashes) {
    return c == '/' && ignoreSlashes || 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' || c == '.' || c == '_' || c == '-' || c == '~';
  }
}
