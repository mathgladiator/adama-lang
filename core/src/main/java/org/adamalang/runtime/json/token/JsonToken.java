/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json.token;

public class JsonToken {
  public final String data;
  public final JsonTokenType type;

  public JsonToken(final JsonTokenType type, final String data) {
    this.type = type;
    this.data = data;
  }
}
