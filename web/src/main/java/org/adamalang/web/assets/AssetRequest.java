/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.adamalang.web.contracts.HttpHandler;

public class AssetRequest {
  public final String space;
  public final String key;
  public final String id;

  public AssetRequest(String space, String key, String id) {
    this.space = space;
    this.key = key;
    this.id = id;
  }

  public static AssetRequest from(HttpHandler.HttpResult result) {
    if (result.asset != null && result.space != null && result.key != null) {
      return new AssetRequest(result.space, result.key, result.asset.id);
    }
    return null;
  }

  public static AssetRequest parse(String uri, String assetKey) throws ErrorCodeException {
    int firstSlash = uri.indexOf('/');
    int lastSlash = uri.lastIndexOf("/id=");
    String space = uri.substring(0, firstSlash);
    String key = uri.substring(firstSlash + 1, lastSlash);
    String encId = uri.substring(lastSlash + 4);
    String id = SecureAssetUtil.decryptFromBase64(SecureAssetUtil.secretKeyOf(assetKey), encId);
    return new AssetRequest(space, key, id);
  }

  public static String extractAssetKey(String cookieHeader) {
    if (cookieHeader != null) {
      for (Cookie cookie : ServerCookieDecoder.STRICT.decode(cookieHeader)) {
        if ("sak".equalsIgnoreCase(cookie.name())) {
          return cookie.value();
        }
      }
    }
    return null;
  }
}
