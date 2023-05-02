/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
