/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.web.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.io.ConnectionContext;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** read headers and ip into a ConnectionContext */
public class ConnectionContextFactory {
  public static ConnectionContext of(final ChannelHandlerContext ctx, HttpHeaders headers) {
    String origin = headers.get("origin");
    if (origin == null) {
      origin = "https://" + headers.get("host");
    }
    String ip = ctx.channel().remoteAddress().toString().replaceAll(Pattern.quote("/"), "");
    String xForwardedFor = headers.get("x-forwarded-for");
    if (xForwardedFor != null && !("".equals(xForwardedFor))) {
      ip = xForwardedFor;
    }
    String cookieHeader = headers.get(HttpHeaderNames.COOKIE);
    TreeMap<String, String> identites = new TreeMap<>();
    if (cookieHeader != null) {
      for (Cookie cookie : ServerCookieDecoder.STRICT.decode(cookieHeader)) {
        if (cookie.name().startsWith("id_") && cookie.isHttpOnly() && cookie.isSecure()) {
          if (identites == null) {
            identites = new TreeMap<>();
          }
          identites.put(cookie.name().substring(3), cookie.value());
        }
      }
    }
    String userAgent = headers.get(HttpHeaderNames.USER_AGENT);
    return new ConnectionContext(origin, ip, userAgent, identites);
  }
}
