/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

/** for URIs of the for /key/uri ; this will parse out the key */
public class KeyPrefixUri {
  public final String key;
  public final String uri;

  public KeyPrefixUri(String key, String uri) {
    this.key = key;
    this.uri = uri;
  }

  public static KeyPrefixUri fromCompleteUri(String uri) {
    int offset = uri.charAt(0) == '/' ? 1 : 0;
    int slashIndex = uri.indexOf('/', offset);
    if (slashIndex > offset) {
      return new KeyPrefixUri(uri.substring(offset, slashIndex), uri.substring(slashIndex));
    } else {
      return new KeyPrefixUri(uri.substring(offset), "/'");
    }
  }
}
