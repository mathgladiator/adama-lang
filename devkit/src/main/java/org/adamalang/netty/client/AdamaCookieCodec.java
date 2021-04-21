/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.client;

import org.adamalang.netty.contracts.ServerOptions;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

/** Encode & Decode the cookies, nom nom nom */
public class AdamaCookieCodec {
  public static final String ADAMA_AUTH_COOKIE_NAME = "adama_token";
  public static final String ADAMA_AUTH_COOKIE_QUERY_STRING_OVERRIDE_NAME = "adama_token_override";

  public static String client(final String name, final String value) {
    return new DefaultCookie(name, value).toString();
  }

  public static String extractCookieValue(final String cookieHeader, final String name) {
    if (cookieHeader != null) {
      final var cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
      for (final Cookie cookie : cookies) {
        if (name.equals(cookie.name())) { return cookie.value(); }
      }
    }
    return null;
  }

  public static String server(final ServerOptions options, final String name, final String value) {
    final var cookie = new DefaultCookie(name, value);
    cookie.setPath("/~socket");
    cookie.setHttpOnly(true); // only the browser can use it, JS is forbiden
    cookie.setSameSite(CookieHeaderNames.SameSite.Strict);
    cookie.setMaxAge(60 * 60 * 24 * 7 * 52); // A whole year
    return ServerCookieEncoder.STRICT.encode(cookie);
  }
}
