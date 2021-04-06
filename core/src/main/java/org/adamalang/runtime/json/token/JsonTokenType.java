/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json.token;

/** defines the type of a token found within a file */
public enum JsonTokenType {
  EndArray(false), // ]
  EndObject(false), // }
  False(false), // false
  Null(false), // null
  NumberLiteralInteger(false), // see data: parse it
  NumberLiteralDouble(false), // see data: parse it
  StartArray(false), // [
  StartObject(false), // {
  StringLiteral(false), // see data: use it
  True(false); // true

  /** is the token hidden from the parse tree */
  public final boolean hidden;

  private JsonTokenType(final boolean hidden) {
    this.hidden = hidden;
  }
}
