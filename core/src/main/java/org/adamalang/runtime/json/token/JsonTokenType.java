/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
