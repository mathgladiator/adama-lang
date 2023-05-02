/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.parser.token;

/** defines the type of a token found within a file */
public enum MajorTokenType {
  /** a comment which be a block /* or // */
  Comment(true),
  /** identifiers: [A-Za-z_][A-Za-z_0-9]* */
  Identifier(false),
  /** special identifiers which are keywords in the language like for, while, do, if, etc... */
  Keyword(false),
  /** a state machine label identifier: '#' [A-Za-z_0-9]* */
  Label(false),
  /**
   * a 'rough' number literal of integers, hex encoded integers, floating point, scientific floating
   * point
   */
  NumberLiteral(false),
  /** a double quoted string literal */
  StringLiteral(false),
  /** a single symbol: '+', '-' */
  Symbol(false),
  /** whitespace like spaces (' '), tabs ('\t'), or newlines (\'n') */
  Whitespace(true);

  /** is the token hidden from the parse tree */
  public final boolean hidden;

  MajorTokenType(final boolean hidden) {
    this.hidden = hidden;
  }
}
