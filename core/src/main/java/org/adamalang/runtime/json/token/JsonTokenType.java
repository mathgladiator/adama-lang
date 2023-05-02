/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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

  JsonTokenType(final boolean hidden) {
    this.hidden = hidden;
  }
}
