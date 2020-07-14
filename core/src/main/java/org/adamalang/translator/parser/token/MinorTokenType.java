/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.token;

/** a token may have a subtype. This is it hint from the lexer to the parser as
 * to how the token must be parsed */
public enum MinorTokenType {
  /** comment is a block based (/* */
  CommentBlock,
  /** comment is a newline based comment // */
  CommentEndOfLine,
  /** the token may be an double due to the presence of . or eE */
  NumberIsDouble,
  /** the token may be an integer */
  NumberIsInteger,
}
