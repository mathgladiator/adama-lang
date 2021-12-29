/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.parser.token;

/** defines the type of a token found within a file */
public enum MajorTokenType {
  /** a comment which be a block /* or // */
  Comment(true),
  /** identifiers: [A-Za-z_][A-Za-z_0-9]* */
  Identifer(false),
  /** special identifiers which are keywords in the language like for, while, do,
   * if, etc... */
  Keyword(false),
  /** a state machine label identifier: '#' [A-Za-z_0-9]* */
  Label(false),
  /** a 'rough' number literal of integers, hex encoded integers, floating point,
   * scientific floating point */
  NumberLiteral(false),
  /** a double quoted string literal */
  StringLiteral(false),
  /** a single symbol: '+', '-' */
  Symbol(false),
  /** whitespace like spaces (' '), tabs ('\t'), or newlines (\'n') */
  Whitespace(true);

  /** is the token hidden from the parse tree */
  public final boolean hidden;

  private MajorTokenType(final boolean hidden) {
    this.hidden = hidden;
  }
}
