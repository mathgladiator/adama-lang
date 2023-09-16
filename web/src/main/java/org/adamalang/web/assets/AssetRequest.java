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
