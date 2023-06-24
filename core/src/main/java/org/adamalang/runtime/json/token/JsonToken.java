/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.json.token;

/** a simple token type paired with data */
public class JsonToken {
  public final String data;
  public final JsonTokenType type;

  public JsonToken(final JsonTokenType type, final String data) {
    this.type = type;
    this.data = data;
  }

  @Override
  public String toString() {
    return "JsonToken{" + "data='" + data + '\'' + ", type=" + type + '}';
  }
}
