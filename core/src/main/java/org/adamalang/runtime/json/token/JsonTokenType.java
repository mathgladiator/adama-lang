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
