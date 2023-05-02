/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.parser.token;

/**
 * a token may have a subtype. This is it hint from the lexer to the parser as to how the token must
 * be parsed
 */
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
