/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.json.token;

public class JsonToken {
  public final String data;
  public final JsonTokenType type;

  public JsonToken(final JsonTokenType type, final String data) {
    this.type = type;
    this.data = data;
  }

  @Override
  public String toString() {
    return "JsonToken{" +
            "data='" + data + '\'' +
            ", type=" + type +
            '}';
  }
}
