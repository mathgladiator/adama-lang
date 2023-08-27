/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import org.adamalang.common.Validators;

/** simple parser for a /SPACE/KEY/URI... request */
public class SpaceKeyRequest {
  public final String space;
  public final String key;
  public final String uri;

  public SpaceKeyRequest(String space, String key, String uri) {
    this.space = space;
    this.key = key;
    this.uri = uri;
  }

  public static SpaceKeyRequest parse(String uri) {
    int firstSlash = uri.indexOf('/');
    if (firstSlash >= 0) {
      int secondSlash = uri.indexOf('/', firstSlash + 1);
      if (secondSlash >= 0) {
        String space = uri.substring(firstSlash + 1, secondSlash);
        if (Validators.simple(space, 127)) {
          int third = uri.indexOf('/', secondSlash + 1);
          String key = third >= 0 ? uri.substring(secondSlash + 1, third) : uri.substring(secondSlash + 1);
          if (Validators.simple(key, 511)) {
            return new SpaceKeyRequest(space, key, third >= 0 ? uri.substring(third) : "/");
          }
        }
      }
    }
    return null;
  }

  public String cacheKey(String parameters) {
    return space + "/" + key + "/" + uri + "?" + parameters;
  }
}
